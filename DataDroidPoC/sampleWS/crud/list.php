<?php 
	require 'include/error.php';
	require 'include/utils.php';
	require 'include/config.php';

	$userUdid = $_GET['userUdid'];

	if ($userUdid == '') {
		showError(ERROR_MISSING_USER);
		return;
	}

	try{
		$pdo_options[PDO::ATTR_ERRMODE] = PDO::ERRMODE_EXCEPTION;
		$db = new PDO(DATABASE_INFO, DATABASE_LOGIN, DATABASE_PASSWORD, $pdo_options);

		// Get the user from the WS
		$response = $db->query(sprintf('SELECT id FROM user WHERE udid = %s', protectFields($userUdid, $db)));

		$row = $response->fetch();
		if ($row == null || count($row) == 0) {	
			// There is no user matching this userId
			// So let's add this new user
			$db->exec(sprintf('INSERT INTO user(udid) VALUES(%s)', protectFields($userUdid, $db)));

			$userId = $db->lastInsertId();

			// Create 2 default entries
			$db->exec(sprintf('INSERT INTO phone(userId,name,manufacturer,androidVersion,screenSize,price) VALUES (\'%s\', \'Nexus One\', \'Google\', \'2.3.4\', \'3.8\', \'300\')', $userId));
			$db->exec(sprintf('INSERT INTO phone(userId,name,manufacturer,androidVersion,screenSize,price) VALUES (\'%s\', \'Sensation\', \'HTC\', \'2.3\', \'4.3\', \'489\')', $userId));
		} else {
			$userId = $row['id'];
		}

		$response = $db->query(sprintf('SELECT * FROM phone WHERE userId = \'%s\'', $userId));

		$phones = array('phones' => array('phone' => array()));
		while($row = $response->fetch()) {
			$phone = array(
				'id' => $row['id'],
				'name' => $row['name'],
				'manufacturer' => $row['manufacturer'],
				'androidVersion' => $row['androidVersion'],
				'screenSize' => $row['screenSize'],
				'price' => $row['price']
			);
			array_push($phones['phones']['phone'], $phone);
		}

		header('Content-type: application/json');
		echo json_encode($phones);

	} catch (Exception $e) {
		showErrorMessage('Erreur : ' . $e->getMessage());
	}
 ?>