package io.github.ktpm.bluemoonmanagement;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("io.github.ktpm.bluemoonmanagement.model.entity")
@EnableJpaRepositories("io.github.ktpm.bluemoonmanagement.repository")
public class SpringBootApp {
    // Không cần gì thêm trong class này
}
