package io.jzheaux.springsecurity.resolutions;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ResolutionInitializer implements SmartInitializingSingleton {
	private final ResolutionRepository resolutions;
	private final UserRepository users;

	public ResolutionInitializer(ResolutionRepository resolutions, UserRepository users) {
	    this.resolutions = resolutions;
	    this.users = users;
	}

	@Override
	public void afterSingletonsInstantiated() {
		this.resolutions.save(new Resolution("Read War and Peace", "user"));
		this.resolutions.save(new Resolution("Free Solo the Eiffel Tower", "user"));
		this.resolutions.save(new Resolution("Hang Christmas Lights", "user"));
		
		 User user = new User("user",
		            "{bcrypt}$2a$10$MywQEqdZFNIYnx.Ro/VQ0ulanQAl34B5xVjK2I/SDZNVGS5tHQ08W");
		 user.grantAuthority("resolution:read");
		 user.grantAuthority("user:read");
		 user.setFullName("User Userson");
		 this.users.save(user);
		 
		 User hasread = new User();
		 hasread.setUsername("hasread");
		 hasread.setPassword("{bcrypt}$2a$10$MywQEqdZFNIYnx.Ro/VQ0ulanQAl34B5xVjK2I/SDZNVGS5tHQ08W");
		 hasread.grantAuthority("resolution:read");
		 hasread.grantAuthority("user:read");
		 hasread.setFullName("Has Read");
		 this.users.save(hasread);

		 User haswrite = new User();
		 haswrite.setUsername("haswrite");
		 haswrite.setPassword("{bcrypt}$2a$10$MywQEqdZFNIYnx.Ro/VQ0ulanQAl34B5xVjK2I/SDZNVGS5tHQ08W");
		 haswrite.grantAuthority("resolution:write");
		 haswrite.setFullName("Has Write");
		 haswrite.addFriend(hasread);
		 haswrite.setSubscription("premium");
		 this.users.save(haswrite);
		 
		 User admin = new User("admin","{bcrypt}$2a$10$bTu5ilpT4YILX8dOWM/05efJnoSlX4ElNnjhNopL9aPoRyUgvXAYa");
		 admin.grantAuthority("ROLE_ADMIN");
		 admin.setFullName("Admin Adminson");
		 this.users.save(admin);
	}
}
