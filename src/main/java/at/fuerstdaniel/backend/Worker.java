package at.fuerstdaniel.backend;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class Worker {
    private final Sequencer sequencer = MidiSystem.getSequencer();;

    public Worker() throws MidiUnavailableException {
    }

    public void startPlay(String filePath) throws MidiUnavailableException, InvalidMidiDataException, IOException {
        Sequence sequence = MidiSystem.getSequence(new File(filePath));
        sequencer.setSequence(sequence);
        for (Track track : sequencer.getSequence().getTracks()) {
            for (int ch = 0; ch < 16; ch++) {
                ShortMessage pc = new ShortMessage();
                pc.setMessage(ShortMessage.PROGRAM_CHANGE, ch, 0, 0); // 24 = Guitar
                track.add(new MidiEvent(pc, 0));
            }
        }
        sequencer.open();
    }

    public void togglePlay() {
        if ((sequencer.isRunning())) {
            sequencer.stop();
        } else {
            sequencer.start();
        }
    }

    public void stopPlay() {
        sequencer.stop();
    }

    public boolean isRunning() {
        return sequencer.isRunning();
    }

    public Sequencer getSequencer() {
        return this.sequencer;
    }
}
