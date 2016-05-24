CREATE KEYSPACE IF NOT EXISTS test
WITH replication = {
	'class' : 'SimpleStrategy',
	'replication_factor' : 1
};

CREATE TABLE test.profile (
	id uuid,
	id_credit uuid,
	id_rent uuid,
	name text,
	age int,
	payment int,
	PRIMARY KEY (id)
);

insert into test.profile(id,id_credit,id_rent,name,age,payment)
values (ef3e4158-c0e0-41bf-b9e9-5f0179183abd,10db07be-fe8b-48cf-9658-c5b12b7ad877,ec445d18-8458-4375-806c-517998039ec9,'Ion',20,100);


insert into test.profile(id,id_credit,id_rent,name,age,payment)
values (235599da-0cae-45b5-b6c8-cc3e614bfa71,8337dcfc-d8ab-4268-b57d-a1644651333b,63c38f3b-2f28-49a7-a831-cdfc26dd9a82,'Vasile',24,140);




