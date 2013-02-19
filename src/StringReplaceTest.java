import org.apache.commons.lang3.StringUtils;

public class StringReplaceTest {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        StringBuffer bigstr = new StringBuffer();
        for (int i = 0; i < 10000000; i++) {
            long time1 = System.currentTimeMillis();
            long time2 = System.currentTimeMillis();
            long time3 = System.currentTimeMillis();
            long time4 = System.currentTimeMillis();
            String res = "" + time1 + "\n" + time2 + "\n" + "\r" + time3 + "\n"
                    + "\r" + time4 + "\n" + "\r";
            // String myres = res.replace("\r", "").replace("\n", "");
            // String myres = StringUtils.replace(res, "\r", "");
            // myres = StringUtils.replace(myres, "\n", "");
            bigstr.append(res);
            if (res.equalsIgnoreCase("just test to avoid optimalization")) {
                System.out.println("Testing testing");
            }
        }
        // String ret = bigstr.toString().replace("\r", "").replace("\n","");
        String ret = StringUtils.replace(bigstr.toString(), "\r", "");
        ret = StringUtils.replace(ret, "\n", "");
        System.out.println("It took : " + (System.currentTimeMillis() - start));
        if (ret.equalsIgnoreCase("just test to avoid optimalization")) {
            System.out.println("Testing testing");
        }

    }
}
