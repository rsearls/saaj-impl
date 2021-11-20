/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
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

package soap;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import jakarta.xml.soap.*;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

public class SetElementNameTest extends TestCase {

	private SOAPMessage sm1;
	private SOAPMessage sm2;
	private SOAPEnvelope envelopeOne;
	private SOAPEnvelope envelopeTwo;

	public SetElementNameTest(String name) {
	        super(name);
    	}

        @Override
	public void setUp() throws Exception {
		createMessageOne();
		createMessageTwo();
	}

	private void createMessageOne() throws Exception {
        	String testDoc =
	 	"<env:Envelope"
                + " xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'>"
                //+ " xmlns:ns1='http://example.com/wsdl'>"
                +    "<env:Header/>"
                +    "<env:Body>"
                +      "<Hello xmlns='http://example.com/wsdl'>"
                +        "<String_1>Duke!</String_1>"
                +        "<String_2>Hi!</String_2>"
                +      "</Hello>"
                +    "</env:Body>"
                + "</env:Envelope>";

        	byte[] testDocBytes = testDoc.getBytes("UTF-8");
        	ByteArrayInputStream bais = 
                	new ByteArrayInputStream(testDocBytes);
	        StreamSource strSource = new StreamSource(bais);
        	MessageFactory mf = MessageFactory.newInstance();
	        sm1 = mf.createMessage();
        	SOAPPart sp = sm1.getSOAPPart();
	        sp.setContent(strSource);
		envelopeOne = sp.getEnvelope();
	}
        
	private void createMessageTwo() throws Exception {
        	String testDoc =
	  	 "<env:Envelope"
                +  " xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'"
                +  " xmlns:ns1='http://example.com/wsdl'>"
                +     "<env:Body>"
                +         "<env:Fault>"
                +             "<faultcode>SOAP-ENV:Server</faultcode>"
                +             "<faultstring>Server Error</faultstring>"
                +             "<detail>"
                +                 "<e:myfaultdetails xmlns:e=\"Some-URI\">"
                +                     "<e:message>"
                +                         "My application didn't work"
                +                     "</e:message>"
                +                     "<errorcode>"
                +                         "1001"
                +                     "</errorcode>"
                +                 "</e:myfaultdetails>"
                +             "</detail>"
		+         "</env:Fault>"
                +     "</env:Body>"
                +"</env:Envelope>";

        	byte[] testDocBytes = testDoc.getBytes("UTF-8");
        	ByteArrayInputStream bais = 
                	new ByteArrayInputStream(testDocBytes);
	        StreamSource strSource = new StreamSource(bais);
        	MessageFactory mf = MessageFactory.newInstance();
	        sm2 = mf.createMessage();
        	SOAPPart sp = sm2.getSOAPPart();
	        sp.setContent(strSource);
		envelopeTwo = sp.getEnvelope();
	}
        
    	public void testSetElementName() throws Exception {
		SOAPBody body = envelopeOne.getBody();
		SOAPHeader header = envelopeOne.getHeader();
		QName newName = new QName("", "newName", "");
		String exception = null;
                try {
			envelopeOne.setElementQName(newName);
		} catch(SOAPException e) {
			exception = e.getMessage();
		}
		assertNotNull("Cannot set a new name for envelope", exception);
		exception = null;
                try {
			header.setElementQName(newName);
		} catch(SOAPException e) {
			exception = e.getMessage();
		}
		assertNotNull("Cannot set a new name for header", exception);
		exception = null;
                try {
			body.setElementQName(newName);
		} catch(SOAPException e) {
			exception = e.getMessage();
		}
		assertNotNull("Cannot set a new name for body", exception);
		Iterator soapBodyElements = body.getChildElements();
		assertTrue("First element is there",
                           soapBodyElements.hasNext());
		SOAPBodyElement soapBodyElement = (SOAPBodyElement)
                                                  soapBodyElements.next();
		assertFalse("First element is the only child element",
                           soapBodyElements.hasNext());
		exception = null;
		try {
			soapBodyElement.setElementQName(newName);
		} catch(SOAPException e) {
			exception = e.getMessage();
		}
		/*} catch(SOAPException e) {
			fail("Changing name of a bodyElement is allowed.");
		}*/
		//assertNotNull("Cannot set a new name for body", exception);
                //soapBodyElement.addNamespaceDeclaration("", "another-uri!!");
                //sm1.writeTo(System.out);
		/*SOAPBodyElement sbe = (SOAPBodyElement) body.getFirstChild();
		assertTrue("New name has been recorded",
                           sbe.getElementQName().equals(newName));
                SOAPElement element = (SOAPElement) sbe.getFirstChild();
                assertTrue("Children are still in place",
                           element.getFirstChild().getNodeValue()
                                                  .equals("Duke!"));*/
    	}

	public void testSetElementNameWithFault() throws Exception {
		SOAPFault fault = envelopeTwo.getBody().getFault();
		QName newName = new QName("newName");
		String exception = null;
                try {
			fault.setElementQName(newName);
		} catch(SOAPException e) {
			exception = e.getMessage();
		}
		assertNotNull("Cannot set a new name for fault", exception);
		Iterator eachChild = fault.getChildElements();
		SOAPFaultElement faultCode = (SOAPFaultElement)
					     eachChild.next();
		exception = null;
                try {
			faultCode.setElementQName(newName);
		} catch(SOAPException e) {
			exception = e.getMessage();
		}
		assertNotNull("Cannot set a new name for faultcode", exception);
		SOAPFaultElement faultString = (SOAPFaultElement)
			  		       eachChild.next();
		exception = null;
                try {
			faultString.setElementQName(newName);
		} catch(SOAPException e) {
			exception = e.getMessage();
		}
		assertNotNull("Cannot set a new name for faultstring",
                              exception);
		Detail detail = (Detail) eachChild.next();
		exception = null;
                try {
			detail.setElementQName(newName);
		} catch(SOAPException e) {
			exception = e.getMessage();
		}
		assertNotNull("Cannot set a new name for detail", exception);
		Iterator detailEntries = detail.getDetailEntries();
		DetailEntry firstEntry = (DetailEntry) detailEntries.next();
		assertFalse("firstEntry is the lastEntry",
			    detailEntries.hasNext()); 
		QName entryNewName = new QName("some-other-uri",
					       "newLocalPart",
					       "newPrefix");
		exception = null;
                try {
			firstEntry.setElementQName(entryNewName);
		} catch(SOAPException e) {
			fail("Changing name of DetailEntry is allowed.");
		}
		Iterator newDetailEntries = detail.getDetailEntries();
		DetailEntry newFirstEntry = (DetailEntry) newDetailEntries.next();
		assertFalse("newFirstEntry is the newLastEntry",
			    newDetailEntries.hasNext());
		Iterator children = newFirstEntry.getChildElements();
		SOAPElement element1 = (SOAPElement) children.next(); 
		SOAPElement element2 = (SOAPElement) children.next(); 
		assertFalse("there are only two child elements",
			    children.hasNext());
		assertTrue("first element has a particular text value",
			   element1.getFirstChild().getNodeValue()
				  .equals("My application didn't work"));
                assertTrue("newDetailEntry has a particular URI",
                           newFirstEntry.getElementQName().getNamespaceURI()
					.equals("some-other-uri"));
		assertTrue("newDetailEntry has imported old attributes",
			   newFirstEntry.getAttributeValue(
                           new QName("", "e", "xmlns")).equals("Some-URI"));
	}
}
