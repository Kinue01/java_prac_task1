package org.example.forms;

import org.example.App;
import org.solution.searadar.mr231_3.convert.Mr231_3Converter;
import org.solution.searadar.mr231_3.station.Mr231_3StationType;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class Mr231_3Form extends JDialog {
    public JPanel panel1;
    private JList<String> list1;
    private JTextField textField1;
    private JButton button1;

    ArrayList<SearadarStationMessage> messages = new ArrayList<>();
    Vector<String> msgList = new Vector<>();
    Mr231_3StationType stationType = new Mr231_3StationType();

    public Mr231_3Form() {
        setContentPane(panel1);
        setModal(true);
        setTitle("Протокол МР-231-3");
        UpdateMessages();
        button1.setText("Отправить");
        button1.addActionListener(e -> SendMessage());
        list1.addListSelectionListener(e -> GetMessageFromList());
    }

    private void UpdateMessages() {
        list1.setModel(new DefaultListModel<>());
        DefaultListModel<String> model = (DefaultListModel<String>) list1.getModel();
        model.removeAllElements();
        messages.clear();
        msgList.clear();
        try {
            Statement st = App.db.getConnection().createStatement();
            ResultSet rs = st.executeQuery("select * from tb_ttm where msgtype = 2");
            while (rs.next()) {
                TargetType type = null;
                TargetStatus status = null;
                IFF iff = null;

                switch (rs.getInt(9)) {
                    case 1:
                        type = TargetType.SURFACE;
                        break;
                    case 2:
                        type = TargetType.AIR;
                        break;
                    case 3:
                        type = TargetType.UNKNOWN;
                        break;
                }
                ;
                switch (rs.getInt(10)) {
                    case 1:
                        status = TargetStatus.LOST;
                        break;
                    case 2:
                        status = TargetStatus.UNRELIABLE_DATA;
                        break;
                    case 3:
                        status = TargetStatus.TRACKED;
                        break;
                }
                ;
                switch (rs.getInt(11)) {
                    case 1:
                        iff = IFF.FRIEND;
                        break;
                    case 2:
                        iff = IFF.FOE;
                        break;
                    case 3:
                        iff = IFF.UNKNOWN;
                        break;
                }
                ;

                TrackedTargetMessage ttm = new TrackedTargetMessage();
                ttm.setMsgRecTime(rs.getTimestamp(1));
                ttm.setMsgTime(rs.getLong(3));
                ttm.setTargetNumber(rs.getInt(4));
                ttm.setDistance(rs.getDouble(5));
                ttm.setBearing(rs.getDouble(6));
                ttm.setCourse(rs.getDouble(7));
                ttm.setSpeed(rs.getDouble(8));
                ttm.setType(type);
                ttm.setIff(iff);
                ttm.setStatus(status);

                messages.add(ttm);
            }
            rs.close();

            rs = st.executeQuery("select * from tb_rsd");
            while (rs.next()) {
                String workingMode = "", distanceUnit = "", displayOrientation = "";

                switch (rs.getInt(11)) {
                    case 1:
                        workingMode = "S";
                        break;
                    case 2:
                        workingMode = "P";
                        break;
                }
                ;

                switch (rs.getInt(9)) {
                    case 1:
                        distanceUnit = "K";
                        break;
                    case 2:
                        distanceUnit = "N";
                        break;
                }
                ;

                switch (rs.getInt(10)) {
                    case 1:
                        displayOrientation = "C";
                        break;
                    case 2:
                        displayOrientation = "H";
                        break;
                    case 3:
                        displayOrientation = "N";
                        break;
                }
                ;

                RadarSystemDataMessage rsd = new RadarSystemDataMessage();
                rsd.setWorkingMode(workingMode);
                rsd.setDisplayOrientation(displayOrientation);
                rsd.setDistanceUnit(distanceUnit);
                rsd.setMsgRecTime(rs.getTimestamp(1));
                rsd.setInitialDistance(rs.getDouble(2));
                rsd.setInitialBearing(rs.getDouble(3));
                rsd.setMovingCircleOfDistance(rs.getDouble(4));
                rsd.setBearing(rs.getDouble(5));
                rsd.setDistanceFromShip(rs.getDouble(6));
                rsd.setBearing2(rs.getDouble(7));
                rsd.setDistanceScale(rs.getDouble(8));

                messages.add(rsd);
            }
            rs.close();

            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            messages.forEach(it -> msgList.add(it.toString()));
            list1.setListData(msgList);
        }
    }

    private void SendMessage() {
        Mr231_3Converter converter = stationType.createConverter();
        List<SearadarStationMessage> res = converter.convert(textField1.getText());

        if (res.get(0).toString().contains("TrackedTargetMessage")) {
            TrackedTargetMessage msg = (TrackedTargetMessage) res.get(0);
            try {
                PreparedStatement st = App.db.getConnection()
                        .prepareStatement("insert into tb_ttm values (?, 2, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                st.setTimestamp(1, msg.getMsgRecTime());
                st.setLong(2, msg.getMsgTime());
                st.setInt(3, msg.getTargetNumber());
                st.setDouble(4, msg.getDistance());
                st.setDouble(5, msg.getBearing());
                st.setDouble(6, msg.getCourse());
                st.setDouble(7, msg.getSpeed());

                switch (msg.getType()) {
                    case SURFACE:
                        st.setInt(8, 1);
                        break;
                    case AIR:
                        st.setInt(8, 2);
                        break;
                    case UNKNOWN:
                        st.setInt(8, 3);
                        break;
                }

                switch (msg.getStatus()) {
                    case LOST:
                        st.setInt(9, 1);
                        break;
                    case UNRELIABLE_DATA:
                        st.setInt(9, 2);
                        break;
                    case TRACKED:
                        st.setInt(9, 3);
                        break;
                }

                switch (msg.getIff()) {
                    case FRIEND:
                        st.setInt(10, 1);
                        break;
                    case FOE:
                        st.setInt(10, 2);
                        break;
                    case UNKNOWN:
                        st.setInt(10, 3);
                        break;
                }

                int result = st.executeUpdate();
                if (result == 1) {
                    ResultDialog resultDialog = new ResultDialog();
                    resultDialog.SetResText("Добавлено успешно");
                    resultDialog.pack();
                    resultDialog.setVisible(true);
                }
                st.close();
                UpdateMessages();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            RadarSystemDataMessage msg = (RadarSystemDataMessage) res.get(0);
            try {
                PreparedStatement statement = App.db.getConnection()
                        .prepareStatement("insert into tb_rsd values " +
                                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setTimestamp(1, msg.getMsgRecTime());
                statement.setDouble(2, msg.getInitialDistance());
                statement.setDouble(3, msg.getInitialBearing());
                statement.setDouble(4, msg.getMovingCircleOfDistance());
                statement.setDouble(5, msg.getBearing());
                statement.setDouble(6, msg.getDistanceFromShip());
                statement.setDouble(7, msg.getBearing2());
                statement.setDouble(8, msg.getDistanceScale());

                switch (msg.getDistanceUnit()) {
                    case "K":
                        statement.setInt(9, 1);
                        break;
                    case "N":
                        statement.setInt(9, 2);
                        break;
                }

                switch (msg.getDisplayOrientation()) {
                    case "C":
                        statement.setInt(10, 1);
                        break;
                    case "H":
                        statement.setInt(10, 2);
                        break;
                    case "N":
                        statement.setInt(10, 3);
                        break;
                }

                switch (msg.getWorkingMode()) {
                    case "S":
                        statement.setInt(11, 1);
                        break;
                    case "P":
                        statement.setInt(11, 2);
                        break;
                }

                int result = statement.executeUpdate();
                if (result == 1) {
                    ResultDialog resultDialog = new ResultDialog();
                    resultDialog.SetResText("Добавлено успешно");
                    resultDialog.pack();
                    resultDialog.setVisible(true);
                }
                statement.close();
                UpdateMessages();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void GetMessageFromList() {
        SearadarStationMessage msg = messages.get(list1.getSelectedIndex());
        if (msg.toString().contains("TrackedTargetMessage")) {
            TrackedTargetMessage ttm = (TrackedTargetMessage) msg;

            String iff = "", type = "", status = "";

            switch (ttm.getIff()) {
                case FRIEND:
                    iff = "b";
                    break;
                case FOE:
                    iff = "p";
                    break;
                case UNKNOWN:
                    iff = "d";
                    break;
            }

            switch (ttm.getStatus()) {
                case LOST:
                    status = "L";
                    break;
                case TRACKED:
                    status = "T";
                    break;
                case UNRELIABLE_DATA:
                    status = "Q";
                    break;
            }

            textField1.setText("$RATTM," + ttm.getTargetNumber() + ","
                    + ttm.getDistance() + ","
                    + ttm.getBearing() + ",T,"
                    + ttm.getSpeed() + ","
                    + ttm.getCourse() + ",T,,,N,"
                    + iff + ","
                    + status + ",,"
                    + ttm.getMsgTime() + ",A*");
        } else {
            RadarSystemDataMessage rsd = (RadarSystemDataMessage) msg;

            String unit = "", orient = "", mode = "";

            switch (rsd.getDistanceUnit()) {
                case "K":
                    unit = "K";
                    break;
                case "N":
                    unit = "N";
                    break;
            }

            switch (rsd.getDisplayOrientation()) {
                case "C":
                    orient = "C";
                    break;
                case "H":
                    orient = "H";
                    break;
                case "N":
                    orient = "N";
                    break;
            }

            switch (rsd.getWorkingMode()) {
                case "S":
                    mode = "S";
                    break;
                case "P":
                    mode = "P";
                    break;
            }

            textField1.setText("$RARSD," + rsd.getInitialDistance() + ","
                    + rsd.getInitialBearing() + ","
                    + rsd.getMovingCircleOfDistance() + ","
                    + rsd.getBearing() + ",,,,,"
                    + rsd.getDistanceFromShip() + ","
                    + rsd.getBearing2() + ","
                    + rsd.getDistanceScale() + ","
                    + unit + ","
                    + orient + ","
                    + mode + "*");
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setPreferredSize(new Dimension(1200, 800));
        list1 = new JList();
        list1.setSelectionMode(0);
        panel1.add(list1, BorderLayout.EAST);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, BorderLayout.NORTH);
        textField1 = new JTextField();
        Font textField1Font = this.$$$getFont$$$(null, -1, 24, textField1.getFont());
        if (textField1Font != null) textField1.setFont(textField1Font);
        panel2.add(textField1, BorderLayout.NORTH);
        button1 = new JButton();
        button1.setText("Button");
        panel2.add(button1, BorderLayout.SOUTH);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
