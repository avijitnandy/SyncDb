<?php 
$user_name = "root";
$password = "";
$server = "localhost";
$db_name = "sqlite_db";

$con = mysqli_connect($server,$user_name,$password,$db_name);
if($con){
	$name = $_POST['name'];
	$query = "insert into users(name) values('".$name."')";
	$result = mysqli_query($con,$query);
	//$response = array();
	
	//print_r($con);
	if($result){
		$status = 'OK';
	}else{
		$status = 'Query FAILED';
	}
}else{
	$status ='Connection Failed';
}

echo json_encode(array("response"=>$status));

mysqli_close($con);
?>