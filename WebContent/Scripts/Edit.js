//ページ読み込みイベントに登録
document.addEventListener("DOMContentLoaded", Main, false);

function Main()
{
	//inputタグのインスタンスを取得
	var input = [];	
	for(var i=0;i<=5;i++)	
		input.push(document.querySelector("input#data"+i));

	//データ送信処理
	var button = document.querySelector("#button");
	button.onclick = function(){
		var datas = {
			"cmd":"write",
			"id":input[0].value,
			"name":input[1].value,
			"area":input[2].value,
			"pc":input[3].value,
			"pop":input[4].value,
			"size":input[5].value	
		};
		AFL.sendJson("JapanMap",datas,onWrite);
		function onWrite(data){
			//リスト更新の要求
			var datas = {"cmd":"list"};
			AFL.sendJson("JapanMap",datas,onList);	
			
			//リストのクリア
			input[0].value = 0;
			for(var i=1;i<=5;i++)
				input[i].value = "";
		}
	}
	
	//一覧の読み出し
	var datas = {"cmd":"list"};
	AFL.sendJson("JapanMap",datas,onList);

	
	//リスト選択
	function onSelectList()	{
		var id = this.value;
		
		//IDが0だった場合は内容をクリア
		if(id == 0){
			input[0].value = 0;
			for(var i=1;i<=5;i++)
				input[i].value = "";
		}
		else{
			//リストが選択されたらデータの要求をする
			var datas = {
					"cmd":"read",
					"id":this.value
				};
			AFL.sendJson("JapanMap",datas,onRead);	
		}

	}
	
	//都道府県リスト更新処理
	function onList(datas){
		var list = document.querySelector("#list");
		list.innerHTML = "";
		var item = document.createElement("div");
		item.id="item";
		item.value = 0;
		item.innerHTML = "新規";
		item.onclick = onSelectList;
		list.appendChild(item);
		
		for(var index in datas){
			var data = datas[index];
			var id = data['id'];
			var name = data['name'];
			
			item = document.createElement("div");
			item.id="item";
			item.value = id;
			item.innerHTML = name;			
			item.onclick = onSelectList;
			list.appendChild(item);
		}
	}
	//入力フォームデータ読み込み
	function onRead(data){
		if(data && data.id){
			input[0].value = data.id;
			input[1].value = data.name;
			input[2].value = data.area;
			input[3].value = data.pc;
			input[4].value = data.pop;
			input[5].value = data.size;
		}
	}			
	
}
