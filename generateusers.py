from faker import Faker

fake = Faker()

for _ in range(500):
    uuid = fake.uuid4().strip()
    username = fake.user_name().strip()
    first_name = fake.first_name().strip()
    last_name = fake.last_name().strip()
    role = fake.random_element(elements=('admin', 'patient')).strip()
    email = fake.email().strip()
    password = fake.password().strip()
    onMedication = fake.boolean(chance_of_getting_true=50)
    dob = fake.date_between(start_date='-75y', end_date='today')
    infection_date = fake.date_between(start_date=dob, end_date='today')
    medication_start_date = fake.date_between(start_date=infection_date, end_date='today')
    country = fake.country().strip()
    dob_formatted = dob.strftime('%a %b %d %H:%M:%S CAT %Y').strip()
    infection_date_formatted = infection_date.strftime('%a %b %d %H:%M:%S CAT %Y').strip()
    medication_start_date_formatted = medication_start_date.strftime('%a %b %d %H:%M:%S CAT %Y').strip()
    print(f"{uuid}:{username}:{password}:{role}:{first_name}:{last_name}:{email}:{infection_date_formatted}:{onMedication}:{medication_start_date_formatted}:{dob_formatted}:{country}")
    