package nl.numworx.oauth2client.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.token.OAuthToken;
import org.json.simple.parser.ParseException;

import io.jsonwebtoken.Claims;


public class UULoginTest {

	UULogin l = new UULogin();

	
	public void test() throws OAuthSystemException {
		System.err.println(l.login(null, null, null, null));
	}

	
	public static void main(String[] args) throws Exception {
		UULoginTest main = new UULoginTest();
//		main.interpret();
//		System.exit(0);
//		main.test();
		UULogin l = new UULogin();
		
		String code;
		
		BufferedInputStream x = new BufferedInputStream(System.in);
		InputStreamReader reader = new InputStreamReader(x);
		BufferedReader r = new BufferedReader(reader);
		System.out.println();
		System.out.print("code>");
		System.out.flush();
		code = r.readLine();
		UULogin.UUClaims result = l.getToken(code);
		System.out.println(result.claims);
	}
	
	
	String token = "eyJraWQiOiI1MTk1MTE0NDU0MTEzOTE0NjE0NTkwODUzNTcyMjA1NjU3MzQ2MjQ3MzAwODA0NDgiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2xvZ2luLmFjYy51dS5ubC9uaWRwL29hdXRoL25hbSIsInN1YiI6IjhmNDkxZWE2ZmU3NDhkNGZmZDhlOGY0OTFlYTZmZTc0IiwiYXVkIjoiODdlMjJiZDktYWM2Ny00NDU3LWI0YzMtYzAzOWM0MmNmMDUyIiwiZXhwIjoxNjIxNjAwNzg4LCJpYXQiOjE2MjE1OTcxODgsImFjciI6InVybjp1dS5ubDppZHA6Y29udHJhY3Q6cGFzc3dvcmQiLCJtYWlsIjoiZi5lLmIuZi50ZXN0Z2RsMDAzQGFjYy51dS5ubCIsInVybjptYWNlOmRpcjphdHRyaWJ1dGUtZGVmOmVkdVBlcnNvbkFmZmlsaWF0aW9uIjoiZW1wbG95ZWUiLCJnaXZlbk5hbWUiOiJGZWIxIiwidXVTaG9ydElEIjoidGVzdEcwMDgiLCJzbiI6InRlc3RHREwwMDMifQ.Zs8-twRZsOE4vRkj56ax85shTfmiE_MTcN-kWhwQ1jZyUhPhT_ArZ4VvX5GEBGnZREooXOEzOpub1ORnMPOzfpPR4fzMFVmX4L-LfE-K3iXjLVS0d3lfCtC5UNcO3H1ARhFA8qhFjGsp4EXvnh0lN7E04aWwJzvNYQVgARKpRfv55PgqlhHc2SdYcyAEQ8PAu6HHqzY8iYSZdi7dFTkN4kom2ouJ0RnAzc3YkJtUIiGymdTNINt_xUv8qUkuABedR_V1OumYWkzjgdfH4KkUMiwoC5jZMpOQceHe6iSoudtFDWQDLSGXQIdTW0loz8p4RA3cP6bW8VmpXyOB9eyXIpnuk9kF7y-qtU8tMW4pTtDYuIM65hCWvpVYGd8uRCUyJC8Q8zB-4iL3bwjyvu-jGLijS_B4REeeXVakz4GTWgpDjYiRoPeJmlkptuWwYY5ByM6kSkt_siDBzZ9f1Oyh7390slXG1RTZvO3yZHyUz_38j9H5lsxQPtDlVy4fLemRXbvncOYgEjV6yB-sFkI5rHd3FBEmvscUzd8QhpaBak4dfJGvo1VnYvZTDwdTQoppbxCExn11TXK-cM4n7it0jVazeXURBcs51hh80v7LN6YLcS09RHJUcW_gj7BroXk2Isa6BCKv1D7cIbeAQ70BcYZIWyNyKkba3MGLzGo98po";
	
	void interpret() {		
		l.idToken(token);
	}
	
	void keys() { 
		try {
			System.out.println(l.getKeys());
		} catch (OAuthSystemException | OAuthProblemException | ParseException e) {
			e.printStackTrace();
		}
	}
	
}
