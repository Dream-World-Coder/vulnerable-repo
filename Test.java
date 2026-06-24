import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.servlet.http.HttpServletRequest;

public class VulnerableBankApp {

    // =====================================================================
    // PART 1: (Entropy & Regex Scanners)
    // =====================================================================

    // 1. Hardcoded Secrets
    // The Entropy Checker should flag this via Shannon entropy analysis[cite: 62].
    private static final String STRIPE_SECRET_KEY =
        "sk_test_4eC39HqLyjWDarjtT1zdp7dc";

    // 2. Weak Cryptography
    public byte[] hashPin(String pin) throws Exception {
        // The Custom Rule Registry should flag MD5 as insecure cryptography.
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(pin.getBytes());
    }

    // =====================================================================
    // PART 2: (Semantics / AST Scanners)
    // =====================================================================

    // 3. SQL Injection (Taint Tracking Required)
    public void executeTransfer(HttpServletRequest request) {
        // Source: Untrusted HTTP input [cite: 36]
        String accountId = request.getParameter("accountId");

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:orcl",
                "admin",
                "password"
            );
            Statement stmt = conn.createStatement();

            // The Semantics Checker must trace 'accountId' across lines to this query.
            String query =
                "SELECT balance FROM accounts WHERE id = " + accountId;

            // Sink: Execution of the tainted variable [cite: 36]
            stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. Command Injection (Taint Tracking Required)
    public void generateReport(HttpServletRequest request) {
        // Source: Untrusted HTTP input [cite: 36]
        String reportName = request.getParameter("filename");

        try {
            // The Semantics Checker must track the string concatenation.
            String command = "sh /opt/scripts/generate_pdf.sh " + reportName;

            // Sink: System execution of attacker-controlled data[cite: 36].
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
