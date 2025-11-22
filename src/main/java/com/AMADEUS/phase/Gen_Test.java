import java.util.Scanner;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
public class Gen_Test {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        Client AMADEUS = Client.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build();

        String Request ="Start";

        while(Request!=null) {
            System.out.println("Ask AMADEUS");
            Request = sc.nextLine();
            if(Request.equals("exit")) {
                break;
            }
            else {
                GenerateContentResponse response =
                        AMADEUS.models.generateContent(
                                "gemini-2.5-flash",
                                Request,
                                null);

                System.out.println(response.text());
            }
        }
    }
}
