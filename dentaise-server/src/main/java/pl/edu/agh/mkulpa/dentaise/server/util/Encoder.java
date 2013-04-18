package pl.edu.agh.mkulpa.dentaise.server.util;

import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class Encoder {
	public static void main(String[] args) {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();
		System.out.println(encoder.encode(args[0]));
	}
}
