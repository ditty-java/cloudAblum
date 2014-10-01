(function(){
			function getClassName(obj,className){
				var aEle = obj.getElementsByTagName('*');
				var arr = [];
				for(var i=0;i<aEle.length;i++){
					if(aEle[i].className==className){
						arr.push(aEle[i]);
					}
				}
				return arr
			}
			function createLi(oSelect,oUlcon,data,cityName,_index){
					var len = data[cityName].length;
					for (var j=0;j<len;j++){
						 var oLi=document.createElement('li');
						 oLi.className = 'list_inner';
						 oLi.innerHTML = data[cityName][j];
						 oUlcon[_index].appendChild(oLi);
						 oUlcon[_index].style.display='block';
					}
			}
	window.citySelect=function(oName,oBtn,oSelect){
			var isAjax=true;
			var oDelete=null
			var oc=null;
			var conLi=null;
			var oUl=null;
			var data={};
			var oUlcon=null;
			var added=false;
			oBtn.onclick = function isCity(ev){
				var oEvent = ev||window.event;
				if(isAjax){
				oUl = document.createElement('div');
				var cityName=this.getAttribute('cityName');
				oUl.className = "uCity";
				oSelect.appendChild(oUl);
				var oImg = '<h3><span>Please select a city you want to check weather.</span><i id="delete">\
				            <img src="./Button_Hotcity_Close.gif"/></i></h3><ul class="list" id="listCon"><li class="active" cityName="hot"\
				            isflag="true">Hot cities</li>\
				            <li cityName="Name_A" isflag="true">ABCDEFG</li><li cityName="Name_H" isflag="true">HIJKL</li>\
				            <li cityName="Name_M" isflag="true">MNOPQRST</li><li cityName="Name_N" isflag="true">WXYZ</li></ul>';
				oUl.innerHTML = oImg;
					oDelete = document.getElementById('delete');
				var listCon = document.getElementById('listCon');
				    conLi = listCon.getElementsByTagName('li');
				for (var i=0;i<conLi.length;i++){
					var oDc=document.createElement('div');
					oDc.className='conDiv';
					oDc.innerHTML='<ul class="city_sort"></ul>';
					oUl.appendChild(oDc);

				}
				data=
				{'hot':['BeiJIng','ShangHai','DaLian','ShenZhen','WuHan','ChengDu','ShengYang','GuiZhou'],'Name_A':['AnQing'],'Name_H':['HuHeHaoTe'],'Name_M':['MuDanJiang'],'Name_N':['NanNing']}
				    oUlcon = getClassName(oSelect,'city_sort')
				var _index=0
				var _isflag = conLi[0].getAttribute('isflag');
				if(_isflag!="false"){
					createLi(oSelect,oUlcon,data,cityName,_index);
					isAjax=false;
					conLi[0].setAttribute("isflag","false");
					}
				isAjax=false;
				}
				oc=getClassName(oSelect,'uCity');
				if(!isAjax){
					oc[0].style.display='block';
				}
				oDelete.onclick = function(){
					oc[0].style.display='none';
				}
				var olist = getClassName(oSelect,'list_inner');
				function innertext(olist){
					for(var j=0;j<olist.length;j++){
					olist[j].onclick = (function(inc){
					return function(){
					oText=this.innerHTML;
					oName.value=oText;
					oUl.style.display='none';
					        }
						})(j);
					  }
					}
					innertext(olist);
				for(var i=0;i<conLi.length;i++){
					conLi[i].onclick = (function(index){
						return function(){
							var _index=index;
							var _flag = this.getAttribute("isflag");
							var cityName=this.getAttribute("cityName");
							if(_flag!="false"){
							createLi(oSelect,oUlcon,data,cityName,_index);
							conLi[index].setAttribute("isflag",false);
							}
							for(var c=0;c<conLi.length;c++){
							  	conLi[c].className="";
							  	oUlcon[c].style.display="none";
							 };
							 conLi[index].className="active";
							 oUlcon[index].style.display="block";
								var olist = getClassName(oSelect,'list_inner');
								innertext(olist);
						  }
						})(i);
					}
				oEvent.cancelBubble=true;
				}
			if(added)return;		
			var oL=document.createElement('link');
			oL.type='text/css';
			oL.rel='stylesheet';
			oL.href='./city.css';
			var oHead=document.getElementsByTagName('head')[0];
			oHead.appendChild(oL);
			added=true;
			}
})()
