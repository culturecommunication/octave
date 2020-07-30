
package ch.docuteam.packer.assertj.swing;

import static org.assertj.swing.timing.Pause.pause;
import static org.assertj.swing.timing.Timeout.timeout;

import java.util.regex.Pattern;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.TextDisplayFixture;
import org.assertj.swing.timing.Condition;

public abstract class AssertionHelper {

    @SuppressWarnings("rawtypes")
    public static void assertText(TextDisplayFixture component, String text, int timeout) {
        pause(new Condition("") {

            @Override
            public boolean test() {
                return component.text().equals(text);
            }

        }, timeout(timeout));
    }

    @SuppressWarnings("rawtypes")
    public static void assertText(TextDisplayFixture component, Pattern textPattern, int timeout) {
        pause(new Condition("") {

            @Override
            public boolean test() {
                return textPattern.matcher(component.text()).matches();
            }

        }, timeout(timeout));
    }

    public static void assertTitle(FrameFixture frame, String title, int timeout) {
        pause(new Condition("") {

            @Override
            public boolean test() {
                return frame.target().getTitle().equals(title);
            }

        }, timeout(timeout));
    }

    public static void assertSelectedRowValue(String text, JXTreeTableComponentFixture tree,
            int timeout) {
        pause(new Condition("The selected row is not " + text) {

            @Override
            public boolean test() {
                return tree.getSelectedRowValue().equals(text);
            }

        }, timeout(timeout));
    }

}
