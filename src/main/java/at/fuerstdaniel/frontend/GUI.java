package at.fuerstdaniel.frontend;

import at.fuerstdaniel.backend.MidiEventStream;
import at.fuerstdaniel.backend.Worker;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class GUI {
    static void main() throws MidiUnavailableException {
        Worker w = new Worker();
        final File[] file = new File[1];

        JFrame frame = new JFrame("MIDI Interpreter");
        frame.setSize(475, 600);
        frame.setVisible(true);
        JTextField text = new JTextField("Current File: ");
        frame.setResizable(false);


        JButton selectFileButton = new JButton("Select MIDI File");
        selectFileButton.addActionListener(_ -> {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file[0] = fc.getSelectedFile();
                    if (file[0].getName().endsWith(".mid")) {
                        text.setText("Current File: " + file[0].getName());
                        try {
                            w.startPlay(file[0].getPath());
                        } catch (MidiUnavailableException | IOException | InvalidMidiDataException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        IO.println("wrong filetype");
                    }

                }
        });

        frame.add(selectFileButton);
        selectFileButton.setBounds(20, 20, 220, 50);
        frame.add(text);
        JButton startStop = new JButton("Play / Pause");
        frame.add(startStop);
        startStop.setBounds(240, 20, 220, 50);
        text.setBounds(20, 70, 440, 50);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        text.setEditable(false);
        JTextField currentNote = new JTextField();
        currentNote.setEditable(false);

        MidiEventStream steam = new MidiEventStream(w.getSequencer());

        new Thread(() -> {
            try {
                while (true) {
                    MidiMessage msg = steam.take();

                    if (msg instanceof ShortMessage sm &&
                            sm.getCommand() == ShortMessage.NOTE_ON &&
                            sm.getData2() > 0) {

                        int note = sm.getData1();

                        SwingUtilities.invokeLater(() ->
                                currentNote.setText("Note: " + note)
                        );
                    }
                }
            } catch (InterruptedException ignored) {}
        }).start();

        frame.add(currentNote);
        currentNote.setBounds(20, 130, 440, 50);

        startStop.addActionListener(_ -> w.togglePlay());
    }
}
