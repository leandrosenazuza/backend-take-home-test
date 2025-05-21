package com.noom.interview.fullstack.sleep

import com.noom.interview.fullstack.sleep.SleepApplication.Companion.UNIT_TEST_PROFILE
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles(UNIT_TEST_PROFILE)
class SleepApplicationTests {

	@Test
	fun contextLoads() {
		Assertions.assertThat(true).isTrue()
	}

}
