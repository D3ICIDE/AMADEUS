import java.awt.*;
import java.awt.datatransfer.*;
public class ClipBoard_test {
    public static void main(String[] args) throws Exception {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String result = (String) clipboard.getData(DataFlavor.stringFlavor);
        System.out.println("Clipboard contents: " + result);


    }
}
