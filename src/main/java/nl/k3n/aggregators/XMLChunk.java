/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.k3n.aggregators;

import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLChunk {
    public final QName Name;
    public final List<XMLEvent> Elements;

    public XMLChunk(QName elementName) {
        this.Name = elementName;
        this.Elements = new LinkedList<>();
    }
    
}
