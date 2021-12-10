/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2021 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/**
*
* @author SAAJ RI Development Team
*/
package com.sun.xml.messaging.saaj.soap.ver1_2;

import java.util.logging.Logger;
import java.util.logging.Level;

import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPException;

import jakarta.xml.soap.*;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import org.w3c.dom.Element;

public class Envelope1_2Impl extends EnvelopeImpl {

    protected static final Logger log =
        Logger.getLogger(Envelope1_2Impl.class.getName(),
                         "com.sun.xml.messaging.saaj.soap.ver1_2.LocalStrings");
    
    public Envelope1_2Impl(SOAPDocumentImpl ownerDoc, String prefix) {
        super(ownerDoc, NameImpl.createEnvelope1_2Name(prefix));
    }

    public Envelope1_2Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    public Envelope1_2Impl(
        SOAPDocumentImpl ownerDoc,
        String prefix,
        boolean createHeader,
        boolean createBody)
        throws jakarta.xml.soap.SOAPException {
        super(
            ownerDoc,
            NameImpl.createEnvelope1_2Name(prefix),
            createHeader,
            createBody);
    }

    @Override
    protected NameImpl getBodyName(String prefix) {
        return NameImpl.createBody1_2Name(prefix);
    }

    @Override
    protected NameImpl getHeaderName(String prefix) {
        return NameImpl.createHeader1_2Name(prefix);
    }

    /*
     * Override setEncodingStyle of ElementImpl to restrict adding encodingStyle
     * attribute to SOAP Envelope (SOAP 1.2 spec, part 1, section 5.1.1)
     */
    @Override
    public void setEncodingStyle(String encodingStyle) throws jakarta.xml.soap.SOAPException {
        log.severe("SAAJ0404.ver1_2.no.encodingStyle.in.envelope");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Envelope");
    }

    /*
     * Override addAttribute of ElementImpl to restrict adding encodingStyle
     * attribute to SOAP Envelope (SOAP 1.2 spec, part 1, section 5.1.1)
     */
    @Override
    public jakarta.xml.soap.SOAPElement addAttribute(jakarta.xml.soap.Name name, String value)
        throws jakarta.xml.soap.SOAPException {
        if (name.getLocalName().equals("encodingStyle")
            && name.getURI().equals(jakarta.xml.soap.SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
            setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }

    @Override
    public jakarta.xml.soap.SOAPElement addAttribute(QName name, String value)
        throws jakarta.xml.soap.SOAPException {
        if (name.getLocalPart().equals("encodingStyle")
            && name.getNamespaceURI().equals(jakarta.xml.soap.SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
            setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }


    /*
     * Override addChildElement method to ensure that no element
     * is added after body in SOAP 1.2.
     */
    @Override
    public jakarta.xml.soap.SOAPElement addChildElement(jakarta.xml.soap.Name name) throws jakarta.xml.soap.SOAPException {
        // check if body already exists
        if (getBody() != null) {
            log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
            throw new SOAPExceptionImpl(
                "Body must be the last element in" + " SOAP Envelope");
        }
        return super.addChildElement(name);
    }

    @Override
    public jakarta.xml.soap.SOAPElement addChildElement(QName name) throws jakarta.xml.soap.SOAPException {
        // check if body already exists
        if (getBody() != null) {
            log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
            throw new SOAPExceptionImpl(
                "Body must be the last element in" + " SOAP Envelope");
        }
        return super.addChildElement(name);
    }


    /*
     * Ideally we should be overriding other addChildElement() methods as well
     * but we are not adding them here since internally all those call the
     * method addChildElement(Name name).
     * In future, if this behaviour changes, then we would need to override
     * all the rest of them as well.
     *
     */

    @Override
    public jakarta.xml.soap.SOAPElement addTextNode(String text) throws SOAPException {
        log.log(
            Level.SEVERE,
            "SAAJ0416.ver1_2.adding.text.not.legal",
            getElementQName());
        throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Envelope is not legal");
    }
}
