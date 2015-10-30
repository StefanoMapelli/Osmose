
package org.datacontract.schemas._2004._07.osmosewebservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.datacontract.schemas._2004._07.osmosewebservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _StopRecordingParameters_QNAME = new QName("http://schemas.datacontract.org/2004/07/OsmoseWebService", "StopRecordingParameters");
    private final static QName _StartRecordingParameters_QNAME = new QName("http://schemas.datacontract.org/2004/07/OsmoseWebService", "StartRecordingParameters");
    private final static QName _StartRecordingParametersEventType_QNAME = new QName("http://schemas.datacontract.org/2004/07/OsmoseWebService", "EventType");
    private final static QName _StartRecordingParametersRecordingId_QNAME = new QName("http://schemas.datacontract.org/2004/07/OsmoseWebService", "RecordingId");
    private final static QName _StartRecordingParametersUserName_QNAME = new QName("http://schemas.datacontract.org/2004/07/OsmoseWebService", "UserName");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.datacontract.schemas._2004._07.osmosewebservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StartRecordingParameters }
     * 
     */
    public StartRecordingParameters createStartRecordingParameters() {
        return new StartRecordingParameters();
    }

    /**
     * Create an instance of {@link StopRecordingParameters }
     * 
     */
    public StopRecordingParameters createStopRecordingParameters() {
        return new StopRecordingParameters();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopRecordingParameters }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/OsmoseWebService", name = "StopRecordingParameters")
    public JAXBElement<StopRecordingParameters> createStopRecordingParameters(StopRecordingParameters value) {
        return new JAXBElement<StopRecordingParameters>(_StopRecordingParameters_QNAME, StopRecordingParameters.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartRecordingParameters }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/OsmoseWebService", name = "StartRecordingParameters")
    public JAXBElement<StartRecordingParameters> createStartRecordingParameters(StartRecordingParameters value) {
        return new JAXBElement<StartRecordingParameters>(_StartRecordingParameters_QNAME, StartRecordingParameters.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/OsmoseWebService", name = "EventType", scope = StartRecordingParameters.class)
    public JAXBElement<String> createStartRecordingParametersEventType(String value) {
        return new JAXBElement<String>(_StartRecordingParametersEventType_QNAME, String.class, StartRecordingParameters.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/OsmoseWebService", name = "RecordingId", scope = StartRecordingParameters.class)
    public JAXBElement<String> createStartRecordingParametersRecordingId(String value) {
        return new JAXBElement<String>(_StartRecordingParametersRecordingId_QNAME, String.class, StartRecordingParameters.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/OsmoseWebService", name = "UserName", scope = StartRecordingParameters.class)
    public JAXBElement<String> createStartRecordingParametersUserName(String value) {
        return new JAXBElement<String>(_StartRecordingParametersUserName_QNAME, String.class, StartRecordingParameters.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.datacontract.org/2004/07/OsmoseWebService", name = "UserName", scope = StopRecordingParameters.class)
    public JAXBElement<String> createStopRecordingParametersUserName(String value) {
        return new JAXBElement<String>(_StartRecordingParametersUserName_QNAME, String.class, StopRecordingParameters.class, value);
    }

}
