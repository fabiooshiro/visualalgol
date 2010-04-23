/*
 * @(#)ClassTreeTool.java	1.12 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package com.sun.tools.example.debug.gui;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.jdi.*;
import com.sun.tools.example.debug.event.*;
import com.sun.tools.example.debug.bdi.*;

public class ClassTreeTool extends JPanel {

    private Environment env;

    private ExecutionManager runtime;
    private SourceManager sourceManager;
    private ClassManager classManager;

    private JTree tree;
    private DefaultTreeModel treeModel;
    private ClassTreeNode root;
    private SearchPath sourcePath;

    private CommandInterpreter interpreter;

    private static String HEADING = "CLASSES";

    public ClassTreeTool(Environment env) {

	super(new BorderLayout());

	this.env = env;
	this.runtime = env.getExecutionManager();
	this.sourceManager = env.getSourceManager();

	this.interpreter = new CommandInterpreter(env);

	root = createClassTree(HEADING);
	treeModel = new DefaultTreeModel(root);
	
        // Create a tree that allows one selection at a time.
	
        tree = new JTree(treeModel);
	tree.setSelectionModel(new SingleLeafTreeSelectionModel());

	/******
        // Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                ClassTreeNode node = (ClassTreeNode)
		    (e.getPath().getLastPathComponent());
		if (node != null) {
		    interpreter.executeCommand("view " + node.getReferenceTypeName());
		}
	    }
	});
	******/

	MouseListener ml = new MouseAdapter() {
	    public void mouseClicked(MouseEvent e) {
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		if(selRow != -1) {
		    if(e.getClickCount() == 1) {
			ClassTreeNode node =
			    (ClassTreeNode)selPath.getLastPathComponent();
			// If user clicks on leaf, select it, and issue 'view' command.
			if (node.isLeaf()) {
			    tree.setSelectionPath(selPath);
			    interpreter.executeCommand("view " + node.getReferenceTypeName());
			}
		    }
		}
	    }
	};
	tree.addMouseListener(ml);

	JScrollPane treeView = new JScrollPane(tree);
	add(treeView);

	// Create listener.
	ClassTreeToolListener listener = new ClassTreeToolListener();
	runtime.addJDIListener(listener);
	runtime.addSessionListener(listener);

        //### remove listeners on exit!
    }

    private class ClassTreeToolListener extends JDIAdapter 
                       implements JDIListener, SessionListener {

	// SessionListener

	public void sessionStart(EventObject e) {
	    // Get system classes and any others loaded before attaching.
	    try {
		Iterator iter = runtime.allClasses().iterator();
		while (iter.hasNext()) {
		    ReferenceType type = ((ReferenceType)iter.next());
		    root.addClass(type);
		}
	    } catch (VMDisconnectedException ee) {
		// VM terminated unexpectedly.
	    } catch (NoSessionException ee) {
		// Ignore.  Should not happen.
	    }
	}

	public void sessionInterrupt(EventObject e) {}
	public void sessionContinue(EventObject e) {}

	// JDIListener

        public void classPrepare(ClassPrepareEventSet e) {
	    root.addClass(e.getReferenceType());
	}

	public void classUnload(ClassUnloadEventSet e) {
	    root.removeClass(e.getClassName());
	}

        public void vmDisconnect(VMDisconnectEventSet e) {
	    // Clear contents of this view.
	    root = createClassTree(HEADING);
	    treeModel = new DefaultTreeModel(root);
	    tree.setModel(treeModel);
	}
    }

    ClassTreeNode createClassTree(String label) {
	return new ClassTreeNode(label, null);
    }

    class ClassTreeNode extends DefaultMutableTreeNode {

	private String name;
	private ReferenceType refTy;  // null for package

	ClassTreeNode(String name, ReferenceType refTy) {
	    this.name = name;
	    this.refTy = refTy;
	}

	public String toString() {
	    return name;
	}

	public ReferenceType getReferenceType() {
	    return refTy;
	}
	
	public String getReferenceTypeName() {
	    return refTy.name();
	}

	private boolean isPackage() {
	    return (refTy == null);
	}

	public boolean isLeaf() {
	    return !isPackage();
	}

	public void addClass(ReferenceType refTy) {
	    addClass(refTy.name(), refTy);
	}

	private void addClass(String className, ReferenceType refTy) {
	    if (className.equals("")) {
		return;
	    }
	    int pos = className.indexOf('.');
	    if (pos < 0) {
		insertNode(className, refTy);
	    } else {
		String head = className.substring(0, pos);
		String tail = className.substring(pos + 1);
		ClassTreeNode child = insertNode(head, null);
		child.addClass(tail, refTy);
	    }
	}

	private ClassTreeNode insertNode(String name, ReferenceType refTy) {
	    for (int i = 0; i < getChildCount(); i++) {
		ClassTreeNode child = (ClassTreeNode)getChildAt(i);
		int cmp = name.compareTo(child.toString());
		if (cmp == 0) {
		    // like-named node already exists
		    return child;
		} else if (cmp < 0) {
		    // insert new node before the child
		    ClassTreeNode newChild = new ClassTreeNode(name, refTy);
		    treeModel.insertNodeInto(newChild, this, i);
		    return newChild;
		}
	    }
	    // insert new node after last child
	    ClassTreeNode newChild = new ClassTreeNode(name, refTy);
	    treeModel.insertNodeInto(newChild, this, getChildCount());
	    return newChild;
	}

	public void removeClass(String className) {
	    if (className.equals("")) {
		return;
	    }
	    int pos = className.indexOf('.');
	    if (pos < 0) {
		ClassTreeNode child = findNode(className);
		if (!isPackage()) {
		    treeModel.removeNodeFromParent(child);
		}
	    } else {
		String head = className.substring(0, pos);
		String tail = className.substring(pos + 1);
		ClassTreeNode child = findNode(head);
		child.removeClass(tail);
		if (isPackage() && child.getChildCount() < 1) {
		    // Prune non-leaf nodes with no children.
		    treeModel.removeNodeFromParent(child);
		}
	    }
	}

	private ClassTreeNode findNode(String name) {
	    for (int i = 0; i < getChildCount(); i++) {
		ClassTreeNode child = (ClassTreeNode)getChildAt(i);
		int cmp = name.compareTo(child.toString());
		if (cmp == 0) {
		    return child;
		} else if (cmp > 0) {
		    // not found, since children are sorted
		    return null;
		}
	    }
	    return null;
	}
	
    }

}
