package ch.heigvd.res.caesar.protocol;

/**
 *
 * @author Olivier Liechti
 */
public class Protocol {

    public static final int A_CONSTANT_SHARED_BY_CLIENT_AND_SERVER = 42;

    public String encrypt(String msg, int key)
    {
        String EncryptedMsg = "";
        for(int i = 0; i < msg.length(); i++)
        {
            EncryptedMsg += (char)(msg.charAt(i) + key);
        }
        return EncryptedMsg;
    }

    public String decrypt(String EncryptedMsg, int key)
    {
        String msg = "";
        for(int i = 0; i < EncryptedMsg.length(); i++)
        {
            msg += (char)(EncryptedMsg.charAt(i) - key);
        }
        return msg;
    }

}
