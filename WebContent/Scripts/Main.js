function move(e)
{
	var output = document.querySelector("#output");
	var img = document.querySelector("#img");
	var x = parseInt(e.clientX) - parseInt(img.offsetLeft);
	var y = parseInt(e.clientY) - parseInt(img.offsetTop);
	output.innerHTML = x + "," +  y;
}
function output(value){
	//後でサーバからデータを取得させる
	var info = document.querySelector("div.info");
	info.innerHTML = "データの読み出し中";
	var data = {"cmd":"read","param":value}
	AFL.sendJson("System",data,onRecv);
	
	function onRecv(datas){
		if(!datas){
			info.innerHTML = value+"の読み出し失敗";
		}
		else{
			//ここに返ってきたデータを表示する処理
			info.innerHTML = "成功"
		}	
	}
}