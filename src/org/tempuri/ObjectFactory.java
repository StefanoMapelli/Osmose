
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.datacontract.schemas._2004._07.osmosewebservice.StartRecordingParameters;
import org.datacontract.schemas._2004._07.osmosewebservice.StopRecordingParameters;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tempuri package. 
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

    private final static QName _StartRecordingStartRecordingParameters_QNAME = new QName("http://tempuri.org/", "startRecordingParameters");
    private final static QName _StopRecordingStopRecordingParameters_QNAME = new QName("http://tempuri.org/", "stopRecordingParameters");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tempuri
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StopRecording }
     * 
     */
    public StopRecording createStopRecording() {
        return new StopRecording();
    }

    /**
     * Create an instance of {@link StartRecordingResponse }
     * 
     */
    public StartRecordingResponse createStartRecordingResponse() {
        return new StartRecordingResponse();
    }

    /**
     * Create an instance of {@link StopRecordingResponse }
     * 
     */
    public StopRecordingResponse createStopRecordingResponse() {
        return new StopRecordingResponse();
    }

    /**
     * Create an instance of {@link StartRecording }
     * 
     */
    public StartRecording createStartRecording() {
        return new StartRecording();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartRecordingParameters }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "startRecordingParameters", scope = StartRecording.class)
    public JAXBElement<StartRecordingParameters> createStartRecordingStartRecordingParameters(StartRecordingParameters value) {
        return new JAXBElement<StartRecordingParameters>(_StartRecordingStartRecordingParameters_QNAME, StartRecordingParameters.class, StartRecording.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopRecordingParameters }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "stopRecordingParameters", scope = StopRecording.class)
    public JAXBElement<StopRecordingParameters> createStopRecordingStopRecordingParameters(StopRecordingParameters value) {
        return new JAXBElement<StopRecordingParameters>(_StopRecordingStopRecordingParameters_QNAME, StopRecordingParameters.class, StopRecording.class, value);
    }

}
