package com.example.sgr.posconsend;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.DataForSendToPrinterTSC;
import net.posprinter.utils.BitmapToByteData.BmpType;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.sgr.posconsend.R;

public class PosActivity extends Activity{
	Button bt1,bt2,bt3,bt4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pos_activity);
		setupview();
		addlistener();
	}

	private void addlistener() {
		// TODO Auto-generated method stub
		//按钮bt1，打印文本
		//pos指令中并没有专门的打印文本的指令
		//但是，你发送过去的数据，如果不是打印机能识别的指令，满一行后，就可以自动打印了，或者加上OA换行，也能打印
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.binder.writeDataByYouself(new UiExecute() {

					@Override
					public void onsucess() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onfailed() {
						// TODO Auto-generated method stub

					}
				}, new ProcessData() {

					@Override
					public List<byte[]> processDataBeforeSend() {
						// TODO Auto-generated method stub
						List<byte[]> list=new ArrayList<byte[]>();
						//创建一段我们想打印的文本,转换为byte[]类型，并添加到要发送的数据的集合list中
						String str="Welcome to use the impact and thermal printer manufactured by professional POS receipt printer company!";
						byte[] data1=strTobytes(str);
						list.add(data1);
						//追加一个打印换行指令，因为，pos打印机满一行才打印，不足一行，不打印
						list.add(DataForSendToPrinterPos80.printAndFeedLine());
						return list;
					}
				});
			}
		});



		//按钮bt2的事件
		//打印条码
		//pos的条码打印和TSC的条码打印不太一样
		//你需要在打印条码前，设置好条码的各个属性，如宽，高，HRI等
		bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.binder.writeDataByYouself(new UiExecute() {

					@Override
					public void onsucess() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onfailed() {
						// TODO Auto-generated method stub

					}
				}, new ProcessData() {

					@Override
					public List<byte[]> processDataBeforeSend() {
						// TODO Auto-generated method stub
						List<byte[]> list=new ArrayList<byte[]>();
						//初始化打印机，清除缓存
						list.add(DataForSendToPrinterPos80.initializePrinter());
						//选择对齐方式
						list.add(DataForSendToPrinterPos80.selectAlignment(1));
						//选择HRI文字的位置,1表示HRI在二维码下边
						list.add(DataForSendToPrinterPos80.selectHRICharacterPrintPosition(02));
						//设置条码宽度,参数单位和意义请参考编程手册
						list.add(DataForSendToPrinterPos80.setBarcodeWidth(3));
						//设置条码高度，一般为162
						list.add(DataForSendToPrinterPos80.setBarcodeHeight(162));
						//打印条码，注意，打印条码有2个方法，俩个方法对应的条码类型不一样，使用需要参考编程手册和方法注解
						//UPC-A
						list.add(DataForSendToPrinterPos80.printBarcode(65, 11, "01234567890"));
						//code128需要在内容里指定编码类型{A，{B，{C等
						//list.add(DataForSendToPrinterPos80.printBarcode(65, 10, "{B01234567"));
						//上面的指令只是在flash里绘制了这个条码，打印还需要一个打印指令
						list.add(DataForSendToPrinterPos80.printAndFeedLine());
						return list;
					}
				});

			}
		});


		//bt3，打印光栅位图，推荐打印图片使用此方法，这种打印方式可以更好的打印较大的图片，而不受打印机内存限制
		//去相册选择图像，在onactivityresult里回调，得到一个bitmap对象，然后调用发送printRasteBmp指令
		bt3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent;
				intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				startActivityForResult(intent, 0);
			}
		});

		//bt4,打印二维码，同样，打印二维码，也需要做一些打印前的设置
		//一些必要的设置，需要参考编程手册给的示例，再调用对应的指令的方法
		bt4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.binder.writeDataByYouself(new UiExecute() {

					@Override
					public void onsucess() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onfailed() {
						// TODO Auto-generated method stub

					}
				}, new ProcessData() {

					@Override
					public List<byte[]> processDataBeforeSend() {
						// TODO Auto-generated method stub\
						ArrayList<byte[]> list = new ArrayList<byte[]>();
						//先初始化打印机，清除缓存
						list.add(DataForSendToPrinterPos80.initializePrinter());
						//指定二维码的模型
						list.add(DataForSendToPrinterPos80.SetsTheSizeOfTheQRCodeSymbolModule(3));
						//设置错误级别
						list.add(DataForSendToPrinterPos80.SetsTheErrorCorrectionLevelForQRCodeSymbol(48));
						//存储二维码的数据到打印机的存储区域
						list.add(DataForSendToPrinterPos80.StoresSymbolDataInTheQRCodeSymbolStorageArea("Welcome to Printer Technology to create advantages Quality to win in the future"));
						//打印存储区域的二维码
						list.add(DataForSendToPrinterPos80.PrintsTheQRCodeSymbolDataInTheSymbolStorageArea());
						//打印并换行
						list.add(DataForSendToPrinterPos80.printAndFeedLine());
						//或者调用简单的封装过的打印二维码的方法
						//不同的是，调用上面的分步方法，只要缓存里的数据没有清除，
						//调用PrintsTheQRCodeSymbolDataInTheSymbolStorageArea，就可以直接打印，而不用再次设置二维码内容
						//DataForSendToPrinterPos80.printQRcode(3, 48, "www.net")
						//相当于每次都重新设置了缓存里的二维码内容

						//list.add(DataForSendToPrinterPos80.printQRcode(3, 48, "www.xprint.net"));
						return list;
					}
				});
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==0&&resultCode==RESULT_OK) {
			//通过去图库选择图片，然后得到返回的bitmap对象
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			final String picturePath = cursor.getString(columnIndex);
			cursor.close();
			final Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
		   MainActivity.binder.writeDataByYouself(new UiExecute() {

				@Override
				public void onsucess() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onfailed() {
					// TODO Auto-generated method stub

				}
			}, new ProcessData() {//发送数据的处理和封装

				@Override
				public List<byte[]> processDataBeforeSend() {
					// TODO Auto-generated method stub
					ArrayList<byte[]> list = new ArrayList<byte[]>();
					list.add(DataForSendToPrinterPos80.printRasterBmp(0, bitmap, BmpType.Threshold));
					return list;
				}
			});
		}

	}
	private void setupview() {
		// TODO Auto-generated method stub
		bt1=(Button) findViewById(R.id.button1);
		bt2=(Button) findViewById(R.id.button2);
		bt3=(Button) findViewById(R.id.button3);
		bt4=(Button) findViewById(R.id.button4);

	}
	/**
	 * 字符串转byte数组
	 * */
	public static byte[] strTobytes(String str){
		byte[] b=null,data=null;
		try {
			b = str.getBytes("utf-8");
			data=new String(b,"utf-8").getBytes("gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
}
