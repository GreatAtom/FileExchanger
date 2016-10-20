import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * Created by Dmitry on 19.10.2016.
 */
public class CommonTest {

    @Test
    public void ff() throws Exception {
        String str = "qwerty";
        for(int i=0;i<str.length()+1; i++){
            String s = str.substring(0, i);
            byte[] sb = s.getBytes("UTF-8");
            System.out.println(s +" "+sb.length);
        }
    }
}
