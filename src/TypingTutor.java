import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.io.*;

public class TypingTutor implements KeyListener {
	// constants
	static final Color normalBackground = new Color(238, 238, 238); // light gray
	static final Color normalForeground = new Color(0, 0, 0); // black
	static final Color highlightBackground = new Color(0, 0, 0); // black
	static final Color highlightForeground = new Color(255, 0, 0); // red
	static final String[] Pangrams = {
		"Jim quickly realized that the beautiful gowns are expensive.",
		"How razorback-jumping frogs can level six piqued gymnasts!",
		"All questions asked by five watched experts amaze the judge.",
		"Jackie will budget for the most expensive zoology equipment.",
		"The quick brown fox jumps over the lazy dog."
	};

	// fields

	// GUI related
	JFrame frame;
	JLabel myLabel; // a label of debug use
	JLabel hintLabel;
	JLabel hintLabel2;
	JTextArea displayArea;
	JPanel keyboardPanel;
	JButton[][] keyboardKeys;

	// typing assessment related
	int targetPangram;
	TypingAssessment typingAssessment;

	// main program entry
	public static void main(String[] argv) {
		TypingTutor myTypingTutor = new TypingTutor(640, 480);
	}

	// constructor
	public TypingTutor(int width, int height) {

		// initialize field: typingAssessment
		pickTargetPangram(); // initialize targetPangram integer
		typingAssessment = new TypingAssessment(Pangrams[targetPangram]);

		// initialize field: frame
		frame = new JFrame("typing tutor");
		frame.addWindowListener(new WindowMonitor());
		frame.setSize(width, height);
		frame.addKeyListener(this);
		frame.setLayout(null); // TODO
		
		// initialize field: myLabel
		myLabel = new JLabel("debug area");
		myLabel.setSize(200, 30);
		myLabel.setLocation(100, 60);

		// initialize field: hintLabel
		hintLabel = new JLabel("type this: " + Pangrams[targetPangram]);
		hintLabel.setSize(600, 20);
		hintLabel.setLocation(20, 5);
		hintLabel.setForeground(Color.RED);

		hintLabel2 = new JLabel("(press ENTER to start over from the beginning of the sentence)");
		hintLabel2.setSize(600, 20);
		hintLabel2.setLocation(20, 25);


		// initialize field: displayArea
		displayArea = new JTextArea(""); // default no text
		displayArea.setSize(600, 150);
		displayArea.setLocation(20, 50);
		displayArea.setFocusable(true);
		displayArea.addKeyListener(this);
		displayArea.setFocusTraversalKeysEnabled(false);

		// initialize keyboardPanel
		keyboardPanel = new JPanel();
		// keyboardPanel.setBackground();
		keyboardPanel.setSize(600, 240);
		keyboardPanel.setLocation(20, 210);
		keyboardPanel.setLayout(null);
		// initialize keyboardKeys
		keyboardKeys = new JButton[Keyboard.Keys.length][];
		int widthUnit = 600 / Keyboard.TotalWidth; // actually = 600/30
		int heightUnit = 240 / Keyboard.Keys.length;
		// process lines of keys
		for(int i=0; i<Keyboard.Keys.length; i++) {
			keyboardKeys[i] = new JButton[Keyboard.Keys[i].length];
			// determine the start position for this line of keys
			int startX = 0;
			int startY = i * (240 / Keyboard.Keys.length); // will be 0, 48, 96, 144, 192
			// process each key
			for(int j=0; j<Keyboard.Keys[i].length; j++) {
				// create the button
				keyboardKeys[i][j] = new JButton(Keyboard.Keys[i][j]);
				keyboardKeys[i][j].setLocation(startX + Keyboard.startPos[i][j] * widthUnit, startY);
				keyboardKeys[i][j].setSize(widthUnit * Keyboard.width[i][j], heightUnit);
				keyboardKeys[i][j].setOpaque(true);
				keyboardPanel.add(keyboardKeys[i][j]);
			}
		}


		// add components to frame
		// frame.add(myLabel); // this is just a debug label
		frame.add(hintLabel);
		frame.add(hintLabel2);
		frame.add(displayArea);
		frame.add(keyboardPanel);

		// display frame
		frame.setVisible(true);
		frame.setFocusable(true);
		frame.setFocusTraversalKeysEnabled(false);
	}

	private void pickTargetPangram() {
		Random r = new Random();
		targetPangram = r.nextInt(Pangrams.length);
	}

	// monitor window closing
	class WindowMonitor extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			try {
				typingAssessment.writeReportToFile();
			} catch(IOException exp) {
				System.err.println("cannot write to file!");
			}
			System.exit(0);
		}
	}

	// keyTyped event handler
	public void keyTyped(KeyEvent e) {
		// the unicode character is sent to the system
		// handle VK_BACK_SPACE
		// if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
		// 	// do nothing
		// 	return;
		// }
		if(e.getKeyChar() == '\n') {
			System.err.println("keyTyped(): getting new-line!!");
		}
		if(e.getKeyChar() == '\b') {
			if(displayArea.getText().length() == 0)
				return; // nothing to backspace
			displayArea.setText(displayArea.getText().substring(0, displayArea.getText().length() - 1));
		} else {
			displayArea.setText(displayArea.getText() + e.getKeyChar());
		}
		typingAssessment.typed(e.getKeyChar());
	}

	// keyPressed event handler
	public void keyPressed(KeyEvent e) {
		// debug use
		myLabel.setText("code = >" + e.getKeyCode() + "<, char = >" + e.getKeyChar() + "<");
		// need to highlight the corresponding key on the virtual keyboard
		highlight(e.getKeyCode(), true);
		/*
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			myLabel.setText("the key: BACKSPACE is pressed.");
			// do the backspace effect
			// String old_text = displayArea.getText();
			// System.out.println("old text: " + old_text);
			// System.out.println("old length: " + old_text.length());
			// System.out.println("last character: >" + old_text.charAt(old_text.length()-1) + "<");
			// displayArea.setText(old_text.substring(0, old_text.length() - 1));
		} else {
			myLabel.setText("the key: " + e.getKeyChar() + " is pressed.");
		}
		*/
	}

	// keyReleased event handler
	public void keyReleased(KeyEvent e) {
		// debug use
		myLabel.setText("code = >" + e.getKeyCode() + "<, char = >" + e.getKeyChar() + "<");
		// need to undo the highlight
		highlight(e.getKeyCode(), false);
		/*
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			myLabel.setText("the key: BACKSPACE is released.");
		} else {
			myLabel.setText("the key: " + e.getKeyChar() + " is released.");
		}
		*/
	}

	// an utility function to highlight or undo the highlight of a virtual key
	private void highlight(int keyCode, boolean toHighlight) {
		// find the index
		int i = 0, j = 0;
		boolean keyFound = false;
		for(i=0; i<Keyboard.Keys.length; i++) {
			for(j=0; j<Keyboard.Keys[i].length; j++) {
				if(Keyboard.KeyCodes[i][j] == keyCode) {
					keyFound = true;
					break;
				}
			}
			if(keyFound)
				break;
		}
		// set the JButton at [i, j]
		if(toHighlight) {
			// set to highlight
			keyboardKeys[i][j].setBackground(highlightBackground);
			keyboardKeys[i][j].setForeground(highlightForeground);
		} else {
			// set to normal
			keyboardKeys[i][j].setBackground(normalBackground);
			keyboardKeys[i][j].setForeground(normalForeground);
		}
	}
}

class Keyboard {
	// the key faces
	public static String[][] Keys = {
		{"~", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "+", "Backspace"},
		{"Tab", "Q", "W", "E","R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\"},
		{"Caps", "A", "S", "D", "F", "G", "H", "J", "K", "L", ":", "'", "Enter"},
		{"Shift", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "?", "^"},
		{"", "<", "V", ">"}
	};
	// the start index (from the left)
	public static int[][] startPos = {
		{0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26},
		{0, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27},
		{0, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25},
		{0, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 25},
		{8, 23, 25, 27}
	};
	// the width of each key
	public static int[][] width = {
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4},
		{3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4},
		{4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{12, 2, 2, 2}
	};
	// total width
	public static final int TotalWidth = 30;
	// key code mapping
	public static final int[][] KeyCodes = {
		{192, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 45, 61, 8},
		{KeyEvent.VK_TAB, 81, 87, 69, 82, 84, 89, 85, 73, 79, 80, 91, 93, 92},
		{20, 65, 83, 68, 70, 71, 72, 74, 75, 76, 59, 222, 10},
		{16, 90, 88, 67, 86, 66, 78, 77, 44, 46, 47, 38},
		{32, 37, 40, 39}
	};
}

class TypingAssessment {
	// fields
	private String targetSentence;
	private int currentPosition;
	private int correctStrokes;
	private int[] incorrectStrokes;
	private int unknownError;

	// methods

	// constructor
	public TypingAssessment(String sentence) {
		targetSentence = sentence;
		currentPosition = 0;
		correctStrokes = 0;
		incorrectStrokes = new int[26]; // initialized as 0's
		unknownError = 0;
	}

	// to get the assessment report of a string
	public String getReport() {
		String s = "TypingTutor Assessment Report\n\n";
		s += "The target sentence is: " + targetSentence + "\n\n";
		s += "Total # of correct keystrokes: " + correctStrokes + "\n";
		// get the sum of incorrect key strokes
		int sum = 0;
		for(int i : incorrectStrokes) {
			sum += i;
		}
		s += "Total # of incorrect keystrokes: " + sum + "\n";
		s += "Overall correctness ratio: " + (double)correctStrokes / (correctStrokes + sum) + "\n\n";
		s += "The keys you have difficulties with are as follows:\n";
		for(int i=0; i<26; i++) {
			if(incorrectStrokes[i] > 0) {
				s += "'" + (char)('a' + i) + "': number of incorrect trials = " + incorrectStrokes[i] + "\n";
			}
		}
		s += "Unknown Error Count = " + unknownError + "\n";
		return s;
	}

	// to write the assessment report to file
	public void writeReportToFile() throws IOException {
		String path = "TypingTutor-AssessmentReport-" + LocalDateTime.now().getMonth() + "-" + LocalDateTime.now().getDayOfMonth() + "-" + LocalDateTime.now().getHour() + "-" + LocalDateTime.now().getMinute();
		// System.err.println("path: " + path);
		File f = new File(path);
		PrintWriter pw = new PrintWriter(f);
		pw.println(getReport());
		pw.close();
	}

	// a character (c) is typed
	public void typed(char c) {
		// if restarting
		if(c == '\n') {
			// restarting from the beginning of the sentence
			System.err.println("TA: restarting");
			currentPosition = 0;
			return;
		}

		// if already at the end of the target sentence
		if(currentPosition == targetSentence.length()) {
			System.err.println("TA: already reach the end, should press ENTER");
			unknownError += 1;
			return;
		}

		// if correct
		if(c == targetSentence.charAt(currentPosition)) {
			System.err.println("TA: correct");
			currentPosition += 1;
			correctStrokes += 1;
		} else {
			// incorrect
			System.err.println("TA: incorrect");
			char targetChar = targetSentence.charAt(currentPosition);
			if(targetChar >= 'a' && targetChar <= 'z') {
				incorrectStrokes[(targetChar - 'a')] += 1;
			} else if(targetChar >= 'A' && targetChar <= 'Z') {
				incorrectStrokes[(targetChar - 'A')] += 1;
			} else {
				unknownError += 1;
			}
		}
	}
}


