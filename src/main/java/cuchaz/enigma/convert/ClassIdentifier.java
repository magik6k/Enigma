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
package cuchaz.enigma.convert;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.jar.JarFile;

import cuchaz.enigma.TranslatingTypeLoader;
import cuchaz.enigma.analysis.JarIndex;
import cuchaz.enigma.convert.ClassNamer.SidedClassNamer;
import cuchaz.enigma.mapping.ClassEntry;
import cuchaz.enigma.mapping.TranslationDirection;
import cuchaz.enigma.mapping.Translator;
import javassist.CtClass;


public class ClassIdentifier {

    private JarIndex index;
    private SidedClassNamer namer;
    private boolean useReferences;
    private TranslatingTypeLoader loader;
    private Map<ClassEntry, ClassIdentity> cache;

    public ClassIdentifier(JarFile jar, JarIndex index, SidedClassNamer namer, boolean useReferences) {
        this.index = index;
        this.namer = namer;
        this.useReferences = useReferences;
        this.loader = new TranslatingTypeLoader(jar, index, new Translator(), new Translator());
        this.cache = Maps.newHashMap();
    }

    public ClassIdentity identify(ClassEntry classEntry)
            throws ClassNotFoundException {
        ClassIdentity identity = this.cache.get(classEntry);
        if (identity == null) {
            CtClass c = this.loader.loadClass(classEntry.getName());
            if (c == null) {
                throw new ClassNotFoundException(classEntry.getName());
            }
            identity = new ClassIdentity(c, this.namer, this.index, this.useReferences);
            this.cache.put(classEntry, identity);
        }
        return identity;
    }
}
