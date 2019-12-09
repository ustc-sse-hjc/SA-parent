package com.test.core.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.test.core.bean.Person;

@Service("personService")
public class GetPersonServiceImpl implements PersonService {
	public List<Person> findPerson() {
		return null;
	}

	@Override
	public List<Person> getPersons() {
		// TODO Auto-generated method stub
		List<Person> persons = new ArrayList<Person>();
//		final String url = "jdbc:mysql://localhost:3306/sa";
//		final String username = "root";
//		final String password = "0000";
		String id, name;
		int age, sex;
		System.out.println("到这里啦");

		try {
			// 1.加载驱动程序
			Class.forName("com.mysql.jdbc.Driver");
			//远程调用
//			Connection conn= DriverManager.getConnection("jdbc:mysql://192.168.1.102:3306/sa?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","guo5251314");
			Connection conn= DriverManager.getConnection("jdbc:mysql://localhost:3306/sa?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","0000");
			// 2.获得数据连接
//			Connection conn = DriverManager.getConnection(url, username, password);
			// 3.使用数据库的连接创建声明
			Statement stmt = conn.createStatement();

			// 4.使用声明执行SQL语句
			ResultSet result = stmt.executeQuery("select * from person");
			// 5.读取数据库的信息
			while (result.next()) { // 若有数据，就输出
				id = result.getString(1);
				name = result.getString(2);
				age = result.getInt(3);
				sex = result.getInt(4);
				// 显示出每一行数据
				System.out.println("person:" + id + "  " + name + "  " + age + "  " + sex);
				Person person = new Person();
				person.setAge(age);
				person.setId(id);
				person.setName(name);
				person.setSex(sex);

				persons.add(person);
			}
			result.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("persons :" + persons);
		return persons;
	}

}
