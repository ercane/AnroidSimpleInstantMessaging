package messaging.mqtt.android.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import messaging.mqtt.android.R;
import messaging.mqtt.android.service.AsimService;

/**
 * Created by mree on 07.12.2016.
 */

public class ChangeBrokerView{

    private Context context;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    public ChangeBrokerView(Context context){
        this.context = context;
        init();
    }

    private void init(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.change_broker_layout, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(view);

        final EditText protocol = (EditText) view.findViewById(R.id.brokerProtocol);
        final EditText ip = (EditText) view.findViewById(R.id.brokerIp);
        final EditText port = (EditText) view.findViewById(R.id.brokerPort);
        final Button changeBtn = (Button) view.findViewById(R.id.btnChange);
        protocol.setText(AsimService.getPreferencesService().getBrokerProtocol());
        ip.setText(AsimService.getPreferencesService().getBrokerIp());
        port.setText(AsimService.getPreferencesService().getBrokerPort());

        changeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!TextUtils.isEmpty(ip.getText()) && !TextUtils.isEmpty(port.getText()) &&
                        !TextUtils.isEmpty(protocol.getText())) {
                    AsimService.getPreferencesService().setBrokerProtocol(protocol.getText().toString());
                    AsimService.getPreferencesService().setBrokerIp(ip.getText().toString());
                    AsimService.getPreferencesService().setBrokerPort(port.getText().toString());
             /*       new Runnable(){
                        @Override
                        public void run(){
                            AsimService.setMqttInit();
                        }
                    }.run();*/
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Alanları doldurunuz", Toast.LENGTH_LONG);
                }
            }
        });

    }

    public Context getContext(){
        return context;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public AlertDialog.Builder getBuilder(){
        return builder;
    }

    public void setBuilder(AlertDialog.Builder builder){
        this.builder = builder;
    }

    public AlertDialog getDialog(){
        return dialog;
    }

    public void setDialog(AlertDialog dialog){
        this.dialog = dialog;
    }
}
