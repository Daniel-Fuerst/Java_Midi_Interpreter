package at.fuerstdaniel.backend;

import javax.sound.midi.*;
import java.util.concurrent.*;

public class MidiEventStream {

    private final BlockingQueue<MidiMessage> queue = new LinkedBlockingQueue<>();

    public MidiEventStream(Sequencer sequencer) throws MidiUnavailableException {
        Transmitter transmitter = sequencer.getTransmitter();
        transmitter.setReceiver(new Receiver() {
            @Override
            public void send(MidiMessage message, long timeStamp) {
                queue.offer(message);
            }

            @Override
            public void close() {}
        });
    }

    /** Non-blocking: returns null if nothing available */
    public MidiMessage poll() {
        return queue.poll();
    }

    /** Blocking: waits until a MIDI message arrives */
    public MidiMessage take() throws InterruptedException {
        return queue.take();
    }
}
