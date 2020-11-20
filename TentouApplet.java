import com.golden.gamedev.GameLoader;
import com.golden.gamedev.Game;
//import netscape.javascript.*;

public class TentouApplet extends GameLoader {

    protected Game createAppletGame() {
        //JSObject browserWindow = (JSObject) JSObject.getWindow( getApplet() );

        return new Tentoumushi();
    }

}
