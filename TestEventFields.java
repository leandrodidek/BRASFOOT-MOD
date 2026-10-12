package mod.brasfoot;
public class TestEventFields {
    public static void main(String[] args) {
        try {
            best.A ev = new best.A(1);
            ev.S(38); // sets dA via S(int)
            ev.T(1);  // sets dB via T(int)
            System.out.println("em() [dA - Minuto]: " + ev.em());
            System.out.println("en() [dB - Tempo]:  " + ev.en());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
