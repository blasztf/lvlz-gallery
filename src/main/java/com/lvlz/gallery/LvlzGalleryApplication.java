package com.lvlz.gallery;

import com.lvlz.gallery.offline.TestApp2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.lvlz.gallery"})
public class LvlzGalleryApplication {

	public static void main(String[] args) {

      if (args.length > 0) {

		    new TestApp2(args);

      }
	  else {

        SpringApplication.run(LvlzGalleryApplication.class, args);

      }
     
	}

}
