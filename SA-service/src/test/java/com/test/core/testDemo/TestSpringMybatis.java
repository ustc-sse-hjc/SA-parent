package com.test.core.testDemo;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.test.core.bean.Person;
import com.test.core.dao.PersonDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml"})
public class TestSpringMybatis {
	
	@Autowired
	PersonDao personDao;
	@Test
	public void testFind(){
		List<Person> find = personDao.findPerson();
		
		System.out.println(find.toString());
	}
	
	public static void main(String[] args) {
		System.out.println(11);
	}
}
