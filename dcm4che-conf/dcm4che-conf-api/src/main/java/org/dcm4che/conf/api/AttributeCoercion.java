/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4che.conf.api;

import java.io.Serializable;

import org.dcm4che.net.Dimse;
import org.dcm4che.net.TransferCapability.Role;
import org.dcm4che.util.StringUtils;
import org.dcm4che.util.UIDUtils;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 */
public class AttributeCoercion
    implements Serializable, Comparable<AttributeCoercion> {


    private static final long serialVersionUID = 7799241531490684097L;

    private final Condition condition;
    private final String uri;

    public AttributeCoercion(String sopClass, Dimse dimse, Role role,
            String aeTitle, String uri) {
        this.condition = new Condition(sopClass, dimse, role, aeTitle);
        this.uri = uri;
    }

    public final String getSOPClass() {
        return condition.sopClass;
    }

    public final Dimse getDIMSE() {
        return condition.dimse;
    }

    public final Role getRole() {
        return condition.role;
    }

    public final String getAETitle() {
        return condition.aeTitle;
    }

    public final String getURI() {
        return uri;
    }

    public boolean matchesCondition(String sopClass, Dimse dimse, Role role,
            String aeTitle) {
        return condition.matches(sopClass, dimse, role, aeTitle);
    }

    public boolean equalsCondition(AttributeCoercion o) {
        return condition.equals(o.condition);
    }

    @Override
    public int compareTo(AttributeCoercion o) {
        return condition.compareTo(o.condition);
    }

    @Override
    public String toString() {
        return promptTo(new StringBuilder(64), "").toString();
    }

    public StringBuilder promptTo(StringBuilder sb, String indent) {
        String indent2 = indent + "  ";
        StringUtils.appendLine(sb, indent, "AttributeCoercion[dimse: ", condition.dimse);
        StringUtils.appendLine(sb, indent2, "role: ", condition.role);
        if (condition.sopClass != null) {
            sb.append(indent2).append("cuid: ");
            UIDUtils.promptTo(condition.sopClass, sb).append(StringUtils.LINE_SEPARATOR);
        }
        if (condition.aeTitle != null)
            StringUtils.appendLine(sb, indent2, "aet: ", condition.aeTitle);
        StringUtils.appendLine(sb, indent2, "uri: ", uri);
        return sb.append(indent).append(']');
    }

    private static class Condition
            implements Serializable, Comparable<Condition> {

        private static final long serialVersionUID = -8993828886666689060L;

        final String sopClass;
        final Dimse dimse;
        final Role role;
        final String aeTitle;
        final int weight;

        public Condition(String sopClass, Dimse dimse, Role role, String aeTitle) {
            this.sopClass = sopClass;
            this.dimse = dimse;
            this.role = role;
            this.aeTitle = aeTitle;
            this.weight = (aeTitle != null ? 2 : 0)
                    + (sopClass != null ? 1 : 0);
        }

        @Override
        public int compareTo(Condition o) {
            return o.weight - weight;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Condition))
                return false;
            
            Condition other = (Condition) o;
            return this.dimse == other.dimse
                    && this.role == other.role
                    && equals(this.aeTitle, other.aeTitle)
                    && equals(this.sopClass, other.sopClass);
           
        }

        private static boolean equals(Object o1, Object o2) {
            return o1 == o2 || o1 != null && o1.equals(o2);
        }

        public boolean matches(String sopClass, Dimse dimse, Role role,
                String aeTitle) {
            return this.dimse == dimse
                    && this.role == role
                    && matches(this.aeTitle, aeTitle)
                    && matches(this.sopClass, sopClass);
        }

        private static boolean matches(Object o1, Object o2) {
            return o1 == null || o2 == null || o1.equals(o2);
        }

    }

}
