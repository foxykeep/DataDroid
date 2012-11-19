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

		$userRow = $response->fetch();
		if ($userRow == null || count($userRow) == 0) {	
			// There is no user matching this userId
			// return an error
			showError(ERROR_UNKNOWN_USER);
			return;
		}

		$isModification = false;
		$id = isset($_GET['id']) ? $_GET['id'] : '';
		$name = isset($_GET['name']) ? $_GET['name'] : '';
		$manufacturer = isset($_GET['manufacturer']) ? $_GET['manufacturer'] : '';
		$androidVersion = isset($_GET['androidVersion']) ? $_GET['androidVersion'] : '';
		$screenSize = isset($_GET['screenSize']) ? $_GET['screenSize'] : '';
		$price = isset($_GET['price']) ? $_GET['price'] : '';
		
		if ($id != '') {
			$response = $db->query(sprintf('SELECT * FROM phone WHERE id = %s and userId = \'%s\'', 
				protectFields($id, $db),
				$userRow['id']));
				
			$phoneRow = $response->fetch();
			if ($phoneRow == null || count($phoneRow) == 0) {	
				// There is no user matching this phoneId
				// return an error
				showError(ERROR_UNKNOWN_PHONE);
				return;
			}
			
			$isModification = true;
			$phoneId = $phoneRow['id'];
		} else if ($name == '' && !ctype_alnum($name)) {
			showError(ERROR_MISSING_NAME);
			return;
		} else if ($manufacturer == '' && !ctype_alnum($manufacturer)) {
			showError(ERROR_MISSING_MANUFACTURER);
			return;
		} else if ($androidVersion == '' && !ctype_alnum($androidVersion)) {
			showError(ERROR_MISSING_ANDROID_VERSION);
			return;
		} else if ($screenSize == '' && !ctype_alnum($screenSize)) {
			showError(ERROR_MISSING_SCREEN_SIZE);
			return;
		} else if ($price == '' && !ctype_alnum($price)) {
			showError(ERROR_MISSING_PRICE);
			return;
		}

		if ($isModification) {
			$db->exec(sprintf('UPDATE phone SET name=%s,manufacturer=%s,androidVersion=%s,screenSize=%s,price=%s WHERE id=\'%s\'', 
				protectFields($name, $db),
				protectFields($manufacturer, $db),
				protectFields($androidVersion, $db),
				protectFields($screenSize, $db),
				protectFields($price, $db),
				$phoneId));
		} else {
			$db->exec(sprintf('INSERT INTO phone(userId,name,manufacturer,androidVersion,screenSize,price) VALUES (\'%s\',%s,%s,%s,%s,%s)', 
				$userRow['id'],
				protectFields($name, $db),
				protectFields($manufacturer, $db),
				protectFields($androidVersion, $db),
				protectFields($screenSize, $db),
				protectFields($price, $db)));
		
			$phoneId = $db->lastInsertId();
		}

		$phone = array('phone' => array(
			'id' => $phoneId,
			'name' => $name,
			'manufacturer' => $manufacturer,
			'androidVersion' => $androidVersion,
			'screenSize' => $screenSize,
			'price' => $price
		));

		header('Content-type: application/json');
		echo json_encode($phone);

	} catch (Exception $e) {
		showErrorMessage('Erreur : ' . $e->getMessage());
	}
 ?>