package com.test.core.service;


import java.util.List;

import com.test.core.bean.Person;

public interface PersonService {
	
	public List<Person> findPerson();
	
	public List<Person> getPersons();
	
}
