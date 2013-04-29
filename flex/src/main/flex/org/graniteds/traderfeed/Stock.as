package org.graniteds.traderfeed
{
	[RemoteClass(alias="org.graniteds.traderfeed.Stock")]
	[Bindable]
	public class Stock {
	
		public function Stock():void {
		}
		
		public var code:String;
		public var last:Number;
		public var low:Number;
		public var high:Number;
		public var change:Number;
		public var timestamp:Number;
	}
}