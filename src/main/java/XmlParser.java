import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class XmlParser {

    private static final String CONTRACTOR_TAG = "m:properties";
    private static final String CONTACT_INFO_TAG = "d:element";
    private static final String SEPARATOR = ",";
    private static final String QUOTE = "'";
    private static final String PATH_TO_XML_SOURCE = "C:/Users/Alex/Downloads/template (1) (2).xml";
    private static final String PATH_TO_CONTRACTOR_CSV = "src/main/resources/contractor.csv";
    private static final String PATH_TO_CONTACT_INFO_CSV = "src/main/resources/contactInfo.csv";

    private final File xmlSource;
    private final File contractor;
    private final File contactInfo;
    private final BufferedWriter contractorWriter;
    private final BufferedWriter contactInfoWriter;
    private final DocumentBuilderFactory factory;
    private final DocumentBuilder builder;
    private final Document doc;
    private final Set<String> noFields;

    public XmlParser() throws Exception {
        xmlSource = new File(PATH_TO_XML_SOURCE);
        contractor = new File(PATH_TO_CONTRACTOR_CSV);
        contactInfo = new File(PATH_TO_CONTACT_INFO_CSV);

        contractorWriter = new BufferedWriter(new FileWriter(contractor));
        contactInfoWriter = new BufferedWriter(new FileWriter(contactInfo));

        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();
        doc = builder.parse(xmlSource);

        noFields = new HashSet<>();
        noFields.add("КонтактнаяИнформация");
        noFields.add("ДополнительныеРеквизиты");
    }

    public static void main(String[] args) throws Exception {

        XmlParser parser = new XmlParser();

        parser.initFields(CONTRACTOR_TAG, parser.contractorWriter);
        parser.initFields(CONTACT_INFO_TAG, parser.contactInfoWriter);
        parser.insertingValues(CONTRACTOR_TAG, parser.contractorWriter);
        parser.insertingValues(CONTACT_INFO_TAG, parser.contactInfoWriter);

        parser.contractorWriter.close();
        parser.contactInfoWriter.close();
    }

    /**
     * creating a table header
     *
     * @throws IOException
     */
    private void initFields(String tagName, BufferedWriter writer) throws IOException {
        NodeList nodes = doc.getElementsByTagName(tagName);
        NodeList fields = nodes.item(0).getChildNodes();
        for (int columnsCounter = 0; columnsCounter < fields.getLength(); columnsCounter++) {
            Node node = fields.item(columnsCounter);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (!noFields.contains(element.getLocalName())) {
                    writer.write(String.format("%s%s%s", QUOTE, element.getLocalName(), QUOTE));
                    if (columnsCounter != fields.getLength() - 2) {
                        writer.write(SEPARATOR);
                    } else writer.newLine();
                }
            }
        }
    }

    /**
     * inserting a table values
     *
     * @throws IOException
     */
    private void insertingValues(String tagName, BufferedWriter writer) throws IOException {
        NodeList nodes = doc.getElementsByTagName(tagName);
        for (int nodesCounter = 0; nodesCounter < nodes.getLength(); nodesCounter++) {
            NodeList values = nodes.item(nodesCounter).getChildNodes();
            for (int valuesCounter = 0; valuesCounter < values.getLength(); valuesCounter++) {
                Node value = values.item(valuesCounter);
                if (value.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) value;
                    if (!noFields.contains(element.getLocalName())) {
                        writer.write(String.format("%s%s%s", QUOTE, element.getTextContent(), QUOTE));
                        if (valuesCounter != values.getLength() - 2) {
                            writer.write(SEPARATOR);
                        } else writer.newLine();
                    }
                }
            }
        }
    }
}
