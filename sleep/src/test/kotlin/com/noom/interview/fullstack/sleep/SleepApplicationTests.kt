package com.noom.interview.fullstack.sleep

import com.noom.interview.fullstack.sleep.SleepApplication.Companion.UNIT_TEST_PROFILE
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(UNIT_TEST_PROFILE)
class SleepApplicationTests : AbstractTest(){

	@Test
	fun contextLoads() {
		Assertions.assertThat(true).isTrue()
	}

}
