package com.jose.walletapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

//import com.walletconnect.android.internal.common.model.Namespace;
//import com.walletconnect.walletconnectv2.clientsync.session.Session;
//import com.walletconnect.walletconnectv2.clientsync.session.after.params.SessionRequest;
import com.jose.walletapp.helpers.HdWalletHelper;
import com.walletconnect.web3.wallet.client.Wallet;
import com.walletconnect.web3.wallet.client.Web3Wallet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import g.p.smartcalculater.R;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class MyWalletActivity extends Activity {
    private static Context context;
    private TextView myAddress;
    private String myAddressStr;
    private String balanceStr;

    private static boolean isNightModeEnabled = false;
    private TextView totalBalance;
    private TextView totalMaticBalance;
    SwipeRefreshLayout swipeRefreshLayout;
    private Thread checkBalanceThread;
    private LinearLayout usdtAsset;
    //TextView addressTextView = findViewById(R.id.addressTextView);
    //TextView balanceTextView = findViewById(R.id.balanceTextView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.main_activity);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        /*Web3Wallet.INSTANCE.setWalletDelegate(new Web3Wallet.WalletDelegate() {
            @Override
            public void onSessionRequest(@NonNull Wallet.Model.SessionRequest sessionRequest) {

            }

            @Override
            public void onSessionProposal(@NonNull Wallet.Model.SessionProposal sessionProposal) {
                // Show UI to user to approve or reject
                *//*Namespace.Session namespace = new Namespace.Session(
                        Collections.singletonList("eip155:56:<YOUR_WALLET_ADDRESS>"),
                        sessionProposal.getRequiredNamespaces().get("eip155").getMethods(),
                        sessionProposal.getRequiredNamespaces().get("eip155").getEvents()
                );

                Map<String, Namespace.Session> namespaces = new HashMap<>();
                namespaces.put("eip155", namespace);

                Web3Wallet.INSTANCE.approveSession(sessionProposal, namespaces);*//*

            }

            @Override
            public void onAuthRequest(@NonNull Wallet.Model.AuthRequest authRequest) {

            }

            @Override
            public void onError(@NonNull Wallet.Model.Error error) {

            }

            @Override
            public void onConnectionStateChange(@NonNull Wallet.Model.ConnectionState connectionState) {

            }



            @Override
            public void onSessionUpdateResponse(@NonNull Wallet.Model.SessionUpdateResponse sessionUpdateResponse) {

            }

            @Override
            public void onSessionSettleResponse(@NonNull Wallet.Model.SettledSessionResponse settledSessionResponse) {

            }



            @Override
            public void onSessionDelete(@NonNull Wallet.Model.SessionDelete sessionDelete) {

            }



        });

*/

        myAddressStr= HdWalletHelper.getMyAddress(context);
        totalBalance=findViewById(R.id.totalBalance);
        totalMaticBalance=findViewById(R.id.assetFiatValue);
        usdtAsset=findViewById(R.id.assetUsdt);
        usdtAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyWalletActivity.this, SendCryptoActivity.class));
            }
        });



        checkBalanceThread=new Thread(){
            @Override
            public void run() {
                try {
                    BigDecimal balance = HdWalletHelper.getWalletBalance(myAddressStr);
                    BigDecimal balanceMatic = HdWalletHelper.getMaticBalance(myAddressStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            totalBalance.setText(balance != null ? ("$" + balance) : "Error fetching Balance");
                            totalMaticBalance.setText(balanceMatic != null ? ("$" + balanceMatic) : "Error fetching Balance");
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
                catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        };


        // Initial load
        swipeRefreshLayout.setRefreshing(true);
        checkBalanceThread.start();

        // Pull-to-refresh handler
        swipeRefreshLayout.setOnRefreshListener(() -> checkBalanceThread.start());

       // totalBalance.setText(balanceStr!=null?("$"+balanceStr):"Problem fetching balance");

        myAddress=findViewById(R.id.myAddress);
        myAddress.setText(myAddressStr);
        myAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Wallet Address", myAddressStr);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(MyWalletActivity.this, "Address copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleWalletConnectUri(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleWalletConnectUri(getIntent());
    }

    private void handleWalletConnectUri(Intent intent) {
        Uri data = intent.getData();
        if (data != null && "wc".equals(data.getHost())) {
            String wcUri = data.getQueryParameter("uri");
            if (wcUri != null) {
               /* Web3Wallet.INSTANCE.pair(wcUri, result -> {
                    Log.d("WalletConnect", "Paired with dApp");
                    return null;
                }, error -> {
                    Log.e("WalletConnect", "Pairing failed: " *//*+ error.getMessage()*//*);
                    return null;
                });*/
            }
        }
    }


    public void fetchUsdtTransactions(String walletAddress) {
        LinearLayout transactionsLinearLayout=findViewById(R.id.transactions);

        String apiKey = "YOUR_API_KEY";//todo:get api key from snowtrace
        String url = "https://api.snowtrace.io/api"
                + "?module=account"
                + "&action=tokentx"
                + "&contractaddress=0xc7198437980c041c805A1EDcbA50c1Ce5db95118"
                + "&address=" + walletAddress
                + "&page=1"
                + "&offset=20"
                + "&sort=desc"
                + "&apikey=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        JSONObject result = new JSONObject(json);
                        JSONArray transactions = result.getJSONArray("result");

                        for (int i = 0; i < transactions.length(); i++) {
                            JSONObject tx = transactions.getJSONObject(i);
                            String hash = tx.getString("hash");
                            String from = tx.getString("from");
                            String to = tx.getString("to");
                            String value = tx.getString("value");
                            String status = tx.getString("txreceipt_status"); // 1 = success, 0 = fail

                            Log.d("TX", "Hash: " + hash + ", Status: " + status);
                            //add transtaction to UI

                        }//for
                    }
                    catch (Exception e){}
                }
                else{

                }
            }//onResponse
        });
    }


}

