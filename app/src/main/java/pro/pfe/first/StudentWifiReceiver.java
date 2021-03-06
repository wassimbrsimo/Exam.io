package pro.pfe.first;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

public class StudentWifiReceiver extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Student_Lobby mActivity;

    public StudentWifiReceiver(WifiP2pManager mManager,WifiP2pManager.Channel mChannel,Student_Lobby mActivity){
        this.mManager=mManager;
        this.mChannel=mChannel;
        this.mActivity=mActivity;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
            if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context,"Wifi is On",Toast.LENGTH_SHORT).show();

                mActivity.startDiscovery();

            }
            else {
                Toast.makeText(context,"Wifi is OFF",Toast.LENGTH_SHORT).show();
                mActivity.wm.setWifiEnabled(true);
            }
        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(mManager!=null){
                //mManager.requestPeers(mChannel,mActivity.peerListListener);
            }
        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

            if(mManager==null){ return;}
            NetworkInfo netinfo= intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(netinfo.isConnectedOrConnecting()){

                //mActivity.startTimedOutTimer();
                Log.e("NETWORK","Connection info request");
                mManager.requestConnectionInfo(mChannel,mActivity.connectionInfoListener);

            }
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
                    mActivity.intent=intent;
        }
    }

}
