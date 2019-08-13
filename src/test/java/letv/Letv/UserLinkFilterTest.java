package letv.Letv;

import org.junit.Test;

public class UserLinkFilterTest {


    @Test
    public void  getId(){
        String address = "space-uid-8363210.html";
        System.out.println(address.substring(address.lastIndexOf("-")+1,address.lastIndexOf(".")));
    }
}
