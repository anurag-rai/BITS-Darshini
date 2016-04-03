/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.ac.bits.protocolanalyzer.analyzer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.DataLinkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.ac.bits.protocolanalyzer.protocol.Protocol;

/**
 *
 * @author amit
 * @author crygnus
 */

@Component
public class PcapAnalyzer {

    private static String defaultNextPacketType = Protocol.ETHERNET;

    private long sequenceValue = 1;

    private AnalyzerCell nextAnalyzerCell;
    private long packetReadCount = 0;

    public void setNextAnalyzerCell(AnalyzerCell cell) {
        this.nextAnalyzerCell = cell;
    }

    public AnalyzerCell getNextAnalyzerCell() {
        return this.nextAnalyzerCell;
    }

    public void analyzePacket(PacketWrapper currentPacket) {
        currentPacket.setPacketId(sequenceValue);
        sequenceValue++;

        AnalyzerCell cell = getNextAnalyzerCell();
        cell.takePacket(currentPacket);
    }

    public long readFile() {

        String sysFile = System.getProperty("PROTOCOL_DATA_FILE");
        try {
            PcapHandle handle = Pcaps.openOffline(sysFile);
            Packet packet = handle.getNextPacket();
            while (packet != null) {
                packetReadCount++;
                long packetId = packetReadCount;
                String packetType = getPacketType(handle);
                int startByte = 0;
                int endByte = packet.length() - 1;
                PacketWrapper packetWrapper = new PacketWrapper(packet,
                        packetId, packetType, startByte, endByte);
                packetWrapper.setPacketTimestamp(handle.getTimestamp());

                analyzePacket(packetWrapper);
                packet = handle.getNextPacket();
            }
            System.out.println("Final read count = " + packetReadCount);
        } catch (PcapNativeException ex) {
            Logger.getLogger(PcapAnalyzer.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (NotOpenException ex) {
            Logger.getLogger(PcapAnalyzer.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return packetReadCount;
    }

    private String getPacketType(PcapHandle handle) {
        String packetType = defaultNextPacketType;
        if (handle.getDlt().equals(DataLinkType.EN10MB)) {
            packetType = Protocol.ETHERNET;
        }
        return packetType;
    }
}
