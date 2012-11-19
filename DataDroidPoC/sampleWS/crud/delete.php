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

		$ids = isset($_GET['ids']) ? $_GET['ids'] : '';
		
		$idArray = explode(',', $ids);
		$idArrayCount = count($idArray);
		for($i=0; $i<$idArrayCount; $i++) {
			$response = $db->query(sprintf('SELECT * FROM phone WHERE id = %s and userId = \'%s\'', 
				protectFields($idArray[$i], $db),
				$userRow['id']));
				
			$phoneRow = $response->fetch();
			if ($phoneRow == null || count($phoneRow) == 0) {	
				// There is no user matching this phoneId
				// return an error
				showError(ERROR_UNKNOWN_PHONE);
				return;
			}
		}
		
		$db->exec(sprintf('DELETE FROM phone WHERE id IN (%s)', "'".implode("','", $idArray)."'"));

		$phones = array('phones' => array('phone' => array()));
		for($i=0; $i<$idArrayCount; $i++) {
			array_push($phones['phones']['phone'], array('id' => $idArray[$i]));
		}

		header('Content-type: application/json');
		echo json_encode($phones);

	} catch (Exception $e) {
		showErrorMessage('Erreur : ' . $e->getMessage());
	}
 ?>