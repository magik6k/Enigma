/*******************************************************************************
 * Copyright (c) 2015 Jeff Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * Jeff Martin - initial API and implementation
 ******************************************************************************/
package cuchaz.enigma.analysis;

import com.google.common.collect.Lists;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.enigma.mapping.MethodEntry;
import cuchaz.enigma.mapping.Translator;

public class ClassImplementationsTreeNode extends DefaultMutableTreeNode {

    private Translator deobfuscatingTranslator;
    private ClassEntry entry;

    public ClassImplementationsTreeNode(Translator deobfuscatingTranslator, ClassEntry entry) {
        this.deobfuscatingTranslator = deobfuscatingTranslator;
        this.entry = entry;
    }

    public ClassEntry getClassEntry() {
        return this.entry;
    }

    public String getDeobfClassName() {
        return this.deobfuscatingTranslator.translateClass(this.entry.getClassName());
    }

    @Override
    public String toString() {
        String className = getDeobfClassName();
        if (className == null) {
            className = this.entry.getClassName();
        }
        return className;
    }

    public void load(JarIndex index) {
        // get all method implementations
        List<ClassImplementationsTreeNode> nodes = Lists.newArrayList();
        for (String implementingClassName : index.getImplementingClasses(this.entry.getClassName())) {
            nodes.add(new ClassImplementationsTreeNode(this.deobfuscatingTranslator, new ClassEntry(implementingClassName)));
        }

        // add them to this node
        nodes.forEach(this::add);
    }

    public static ClassImplementationsTreeNode findNode(ClassImplementationsTreeNode node, MethodEntry entry) {
        // is this the node?
        if (node.entry.equals(entry)) {
            return node;
        }

        // recurse
        for (int i = 0; i < node.getChildCount(); i++) {
            ClassImplementationsTreeNode foundNode = findNode((ClassImplementationsTreeNode) node.getChildAt(i), entry);
            if (foundNode != null) {
                return foundNode;
            }
        }
        return null;
    }
}
