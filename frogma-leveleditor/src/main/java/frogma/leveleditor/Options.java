package frogma.leveleditor;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 * Internal class for handling options in the leveleditor.
 * <p>
 * The class makes a dialog-window where you can change settings in the
 * editor, e.g. tilesizes, tilesets etc...
 *
 * @author Andreas W. Bjerkhaug
 */
class Options extends JDialog {

    private JTextField fgTileSizeArea;
    private JTextField bgTileSizeArea;
    private JTextField rfgTileSizeArea;
    private JTextField fgWidthArea;
    private JTextField fgHeightArea;
    private JTextField bgWidthArea;
    private JTextField bgHeightArea;
    private JTextField rfgWidthArea;
    private JTextField rfgHeightArea;
    private JTextField fgImgArea;
    private JTextField bgImgArea;
    private JTextField rfgImgArea;
    private JTextField musicArea;
    private LevelEditor myEditor;
    private boolean neue;

    /**
     * Standard construcor, makes the dialog window.
     */
    Options(LevelEditor myEditor, boolean neue) {
        this.myEditor = myEditor;
        this.setTitle("Options");
        this.neue = neue;

        JLabel fgTileSizeLabel = new JLabel("Foreground tilesize:");
        JLabel bgTileSizeLabel = new JLabel("Background tilesize:");
        JLabel rfgTileSizeLabel = new JLabel("RForeground tilesize:");
        JLabel fgWidthLabel = new JLabel("Foreground width:");
        JLabel fgHeightLabel = new JLabel("Foreground height:");
        JLabel bgWidthLabel = new JLabel("Background width:");
        JLabel bgHeightLabel = new JLabel("Background height:");
        JLabel rfgWidthLabel = new JLabel("RForeground width:");
        JLabel rfgHeightLabel = new JLabel("RForeground height:");
        JLabel fgImgLabel = new JLabel("Foreground tileset:");
        JLabel bgImgLabel = new JLabel("Background tileset:");
        JLabel rfgImgLabel = new JLabel("RForeground tileset:");
        JLabel musicLabel = new JLabel("Midi-file:");

        fgTileSizeArea = new JTextField(myEditor.getLayerTileSize(1) + "");
        fgTileSizeArea.setColumns(4);
        bgTileSizeArea = new JTextField(myEditor.getLayerTileSize(0) + "");
        bgTileSizeArea.setColumns(4);
        rfgTileSizeArea = new JTextField(myEditor.getLayerTileSize(2) + "");
        rfgTileSizeArea.setColumns(4);
        fgWidthArea = new JTextField(myEditor.getLayerWidth(1) + "");
        fgWidthArea.setColumns(4);
        fgHeightArea = new JTextField(myEditor.getLayerHeight(1) + "");
        fgHeightArea.setColumns(4);
        bgWidthArea = new JTextField(myEditor.getLayerWidth(0) + "");
        bgWidthArea.setColumns(4);
        bgHeightArea = new JTextField(myEditor.getLayerHeight(0) + "");
        bgHeightArea.setColumns(4);
        rfgWidthArea = new JTextField(myEditor.getLayerWidth(2) + "");
        rfgWidthArea.setColumns(4);
        rfgHeightArea = new JTextField(myEditor.getLayerHeight(2) + "");
        rfgHeightArea.setColumns(4);
        fgImgArea = new JTextField(myEditor.getTileSetString(1));
        fgImgArea.setColumns(4);
        bgImgArea = new JTextField(myEditor.getTileSetString(0));
        bgImgArea.setColumns(4);
        rfgImgArea = new JTextField(myEditor.getTileSetString(2));
        rfgImgArea.setColumns(4);
        musicArea = new JTextField(myEditor.getBackgroundMusicFilename());
        musicArea.setColumns(4);

        JButton[] browse = new JButton[4];
        browse[0] = new JButton("browse...");
        browse[1] = new JButton("browse...");
        browse[2] = new JButton("browse...");
        browse[3] = new JButton("browse...");

        browse[0].addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Browse...");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fgImgArea.setText(fc.getSelectedFile().getName());
            }
        });

        browse[1].addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Browse...");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                bgImgArea.setText(fc.getSelectedFile().getName());
            }
        });

        browse[2].addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Browse...");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                rfgImgArea.setText(fc.getSelectedFile().getName());
            }
        });

        browse[3].addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Browse...");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                musicArea.setText(fc.getSelectedFile().getName());
            }

        });

        JButton ok = new JButton("Ok");
        ok.addActionListener(e -> ok());

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());

        JPanel panel1 = new JPanel(new GridLayout(13, 3));
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel1.add(fgTileSizeLabel);
        panel1.add(fgTileSizeArea);
        panel1.add(new JLabel(""));
        panel1.add(bgTileSizeLabel);
        panel1.add(bgTileSizeArea);
        panel1.add(new JLabel(""));
        panel1.add(rfgTileSizeLabel);
        panel1.add(rfgTileSizeArea);
        panel1.add(new JLabel(""));
        panel1.add(fgWidthLabel);
        panel1.add(fgWidthArea);
        panel1.add(new JLabel(""));
        panel1.add(fgHeightLabel);
        panel1.add(fgHeightArea);
        panel1.add(new JLabel(""));
        panel1.add(bgWidthLabel);
        panel1.add(bgWidthArea);
        panel1.add(new JLabel(""));
        panel1.add(bgHeightLabel);
        panel1.add(bgHeightArea);
        panel1.add(new JLabel(""));
        panel1.add(rfgWidthLabel);
        panel1.add(rfgWidthArea);
        panel1.add(new JLabel(""));
        panel1.add(rfgHeightLabel);
        panel1.add(rfgHeightArea);
        panel1.add(new JLabel(""));
        panel1.add(fgImgLabel);
        panel1.add(fgImgArea);
        panel1.add(browse[0]);
        panel1.add(bgImgLabel);
        panel1.add(bgImgArea);
        panel1.add(browse[1]);
        panel1.add(rfgImgLabel);
        panel1.add(rfgImgArea);
        panel1.add(browse[2]);
        panel1.add(musicLabel);
        panel1.add(musicArea);
        panel1.add(browse[3]);


        panel2.add(ok);
        panel2.add(cancel);

        this.getContentPane().add(panel1, BorderLayout.WEST);
        this.getContentPane().add(panel2, BorderLayout.EAST);
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }

    /**
     * Method that makes the chosen changes when you press the ok-button-
     */
    private void ok() {
        int fgTileSize;
        try {
            fgTileSize = Integer.parseInt(fgTileSizeArea.getText());
        } catch (NumberFormatException nfe) {
            fgTileSize = 32;
        }


        int bgTileSize;
        try {
            bgTileSize = Integer.parseInt(bgTileSizeArea.getText());
        } catch (NumberFormatException nfe) {
            bgTileSize = 96;
        }

        int rfgTileSize;
        try {
            rfgTileSize = Integer.parseInt(rfgTileSizeArea.getText());
        } catch (NumberFormatException nfe) {
            rfgTileSize = 192;
        }


        int fgWidth;
        int fgHeight;
        try {
            fgWidth = Integer.parseInt(fgWidthArea.getText());
            fgHeight = Integer.parseInt(fgHeightArea.getText());
        } catch (NumberFormatException nfe) {
            fgWidth = 40;
            fgHeight = 40;
        }


        int bgWidth;
        int bgHeight;
        try {
            bgWidth = Integer.parseInt(bgWidthArea.getText());
            bgHeight = Integer.parseInt(bgHeightArea.getText());
        } catch (NumberFormatException nfe) {
            bgWidth = 10;
            bgHeight = 10;
        }


        int rfgWidth;
        int rfgHeight;
        try {
            rfgWidth = Integer.parseInt(rfgWidthArea.getText());
            rfgHeight = Integer.parseInt(rfgHeightArea.getText());
        } catch (NumberFormatException nfe) {
            rfgWidth = 10;
            rfgHeight = 10;
        }

        myEditor.setBackgroundMusicFilename(musicArea.getText());
        myEditor.setImages(fgImgArea.getText(), bgImgArea.getText(), rfgImgArea.getText());

        if (!neue) {
            myEditor.setFgTileSize(fgTileSize);
            myEditor.setBgTileSize(bgTileSize);
            myEditor.setRfgTileSize(rfgTileSize);
            myEditor.fgResize(fgWidth, fgHeight);
            myEditor.bgResize(bgWidth, bgHeight);
            myEditor.rfgResize(rfgWidth, rfgHeight);
        } else {
            myEditor.neue(fgTileSize, bgTileSize, rfgTileSize, fgWidth, fgHeight, bgWidth, bgHeight, rfgWidth, rfgHeight);
        }
        dispose();
    }
}
