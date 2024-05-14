import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
//make text box go next to the label area (change of layout)

public class Jokes implements ActionListener {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private JPanel inputPanel, buttonPanel, sadnessPanel;
    private JMenuBar mb;
    private JMenu file, edit, help;
    private JMenuItem cut, copy, paste, selectAll;
    private JTextPane results;
    private JTextArea numberTextArea;
    private int WIDTH = 400;
    private int HEIGHT = 500;

    public String totalJson = "";

    public String[] jokes;
    public String[] punch;

    public int n;



    public Jokes() {
        prepareGUI();
    }

    public void pull() {
        String output = "abc";

        try {
            URL url = new URL("https://official-joke-api.appspot.com/random_ten");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                totalJson += output;
            }

            conn.disconnect();

        } catch (Exception e) {
            System.out.println(e);
        }


        getJoke();
    }

    public void getJoke() {

        JSONParser parser = new JSONParser();
        //System.out.println(str);

        try {

            org.json.simple.JSONArray jsonArray = (org.json.simple.JSONArray) parser.parse(totalJson);
            System.out.println(jsonArray);
                for (int i = 0; i < n; ++i) {
                    JSONObject joke = (JSONObject) jsonArray.get(i);
                    String setup = (String) joke.get("setup");
                    System.out.println(setup);
                    jokes[i] = setup;
                    String punchline = (String) joke.get("punchline");
                    System.out.println(punchline);
                    punch[i] = punchline;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void prepareGUI() {
        mainFrame = new JFrame("Giggles Guaranteed");
        mainFrame.setSize(WIDTH, HEIGHT);
        mainFrame.setLayout(new BorderLayout());


        cut = new JMenuItem("cut");
        copy = new JMenuItem("copy");
        paste = new JMenuItem("paste");
        selectAll = new JMenuItem("selectAll");
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        selectAll.addActionListener(this);

        mb = new JMenuBar();
        file = new JMenu("File");
        edit = new JMenu("Edit");
        help = new JMenu("Help");
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(selectAll);
        mb.add(file);
        mb.add(edit);
        mb.add(help);


        results = new JTextPane();
        results.setText("Your Jokes:" + "\n\n");
        results.setBounds(50, 5, WIDTH - 100, HEIGHT - 50);
        mainFrame.add(mb);

        JScrollPane scrollPane = new JScrollPane(results);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainFrame.setJMenuBar(mb);

        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setSize(350, 100);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });


        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        numberTextArea = new JTextArea();
        numberTextArea.setBounds(50, 5, WIDTH - 100, HEIGHT - 50);


        sadnessPanel = new JPanel();
        sadnessPanel.setLayout(new BorderLayout());
        inputPanel.add(sadnessPanel, BorderLayout.NORTH);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());

        titleLabel = new JLabel("Sadness Level (1-10): ");
        titleLabel.setLayout(new BorderLayout());
        titleLabel.setBounds(10,10,50, HEIGHT + 100);

        sadnessPanel.add(titleLabel, BorderLayout.WEST);
        sadnessPanel.add(numberTextArea, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.CENTER);

        mainFrame.add(inputPanel, BorderLayout.NORTH);
        mainFrame.add(scrollPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);
        showEventDemo();
    }

    private void showEventDemo() {
        headerLabel.setText("Control in action: Button");

        JButton submitButton = new JButton("Submit");

        submitButton.setActionCommand("Submit");

        submitButton.addActionListener(new ButtonClickListener());

        buttonPanel.add(submitButton, BorderLayout.CENTER);

        JButton punchlineButton = new JButton("Reveal Punchline");

        punchlineButton.setActionCommand("punchline");

        punchlineButton.addActionListener(new ButtonClickListener());

        buttonPanel.add(punchlineButton, BorderLayout.SOUTH);

        mainFrame.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cut)
            results.cut();
        if (e.getSource() == paste)
            results.paste();
        if (e.getSource() == copy)
            results.copy();
        if (e.getSource() == selectAll)
            results.selectAll();
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("Submit")) {
                if(numberTextArea.getText() == ""){
                    results.setText("Please Enter Your Level of Sadness");
                }else {
                    n = Integer.valueOf(numberTextArea.getText());
                    jokes = new String[n];
                    punch = new String[n];
                    pull();
                    for (int x = 0; x < n; ++x) {
                        results.setText(results.getText() + jokes[x] + "\n\n");
                    }
                }
            }

            if (command.equals("punchline")) {
                StyleContext sc = new StyleContext();
                final Style cwStyle = sc.addStyle("ConstantWidth", null);
                StyleConstants.setFontFamily(cwStyle, "MainStyle");
                StyleConstants.setForeground(cwStyle, Color.blue);
                results.setCharacterAttributes(cwStyle, true);
                for (int x = 0; x < n; ++x) {
                    results.setText(results.getText() + punch[x] + "\n\n");
                }

            }
        }
    }


    public static void main(String[] args) {
        Jokes j = new Jokes();
    }
}

