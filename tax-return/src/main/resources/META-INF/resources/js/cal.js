PrimeFaces.locales['tr'] = { closeText: 'kapat', prevText: 'geri', nextText: 'ileri', currentText: 'bugün', monthNames: ['Ocak','Şubat','Mart','Nisan','Mayıs','Haziran','Temmuz','Ağustos','Eylül','Ekim','Kasım','Aralık'], monthNamesShort: ['Oca','Şub','Mar','Nis','May','Haz', 'Tem','Ağu','Eyl','Eki','Kas','Ara'], dayNames: ['Pazar','Pazartesi','Salı','Çarşamba','Perşembe','Cuma','Cumartesi'], dayNamesShort: ['Pz','Pt','Sa','Ça','Pe','Cu','Ct'], dayNamesMin: ['Pz','Pt','Sa','Ça','Pe','Cu','Ct'], weekHeader: 'Hf', firstDay: 1, isRTL: false, showMonthAfterYear: false, yearSuffix: '', timeOnlyTitle: 'Zaman Seçiniz', timeText: 'Zaman', hourText: 'Saat', minuteText: 'Dakika', secondText: 'Saniye', ampm: false, month: 'Ay', week: 'Hafta', day: 'Gün', allDayText : 'Tüm Gün' };


$(document).ready(function() {

	   $('.ui-menuitem-link').each(function(){
	       if(window.location.pathname.indexOf($(this).attr('href')) != -1) {
	           $(this).css('background', '#D8F2E5');//or add class
	       }
	   });  

	})