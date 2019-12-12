
$(function() {
	// リアルタイム値段変更:ラジオボタン
	$('input[name="size"]:radio').change(function() {
		calc_total_price();
	}).trigger('change');

	// リアルタイム値段変更:チェックボックス
	$("input:checkbox").change(function() {
		calc_total_price();
	}).trigger('change');

	// リアルタイム値段変更:セレクトボックス
	$("#quantity").change(function() {
		calc_total_price();
	}).trigger('change');

	//計算用メソッド
	function calc_total_price(){
		size = $("input[name='size']:checked").val();
		if (size == 'M') {
			// Mサイズ商品の価格
			var priceM = $('#price_M').text();
			priceM = priceM.replace( ',', '' );
			priceM = +priceM;
			// トッピング数
			check_count = +$("input[type='checkbox']:checked").length;
			// 数量
			quantity = +$("#quantity").val();
			// 合計金額
			total_price = 0;
			total_price = (priceM + (check_count * 200)) * quantity;
			$("#total_price").text(total_price.toLocaleString());
		} else {
			// Lサイズ商品の価格
			var priceL = $('#price_L').text();
			priceL = priceL.replace( ',', '' );
			priceL = +priceL;
			// トッピング数
			check_count = +$("input[type='checkbox']:checked").length;
			// 数量
			quantity = +$("#quantity").val();
			// 合計金額
			total_price = 0;
			total_price = (priceL + (check_count * 300)) * quantity;
			$("#total_price").text(total_price.toLocaleString());			
		}
	}	
});

	
	