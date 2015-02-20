package com.uninorte.calculadora;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TextView expression = (TextView)findViewById(R.id.text);
        TextView result = (TextView)findViewById(R.id.result);
        outState.putString("expression", expression.getText().toString());
        outState.putString("result", result.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView expression = (TextView)findViewById(R.id.text);
        TextView result = (TextView)findViewById(R.id.result);
        if (savedInstanceState != null){
            expression.setText(savedInstanceState.getString("expression"));
            result.setText(savedInstanceState.getString("result"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void WriteNumber(View view) {

        Button b = (Button) findViewById(view.getId());
        TextView expression = (TextView) findViewById(R.id.text);
        if(expression.getText().equals("")) {
            expression.setText(b.getText().toString());
        }
        else {
            expression.setText(expression.getText().toString() + b.getText());
        }
        //Toast.makeText(this, (String) b.getText(), Toast.LENGTH_SHORT).show();
    }

    public void writeOperation(View view) {

        Button b = (Button) findViewById(view.getId());
        TextView expression = (TextView) findViewById(R.id.text);
        if(!expression.getText().toString().isEmpty())
            if(b.getText().equals("+") || b.getText().equals("*") || b.getText().equals("/") || (b.getText().equals("-") &&
            !expression.getText().toString().substring(expression.length()-2,expression.length()).equals("*-") &&
            !expression.getText().toString().substring(expression.length()-2,expression.length()).equals("/-") &&
            !expression.getText().toString().substring(expression.length()-2,expression.length()).equals("+-") &&
            !expression.getText().toString().substring(expression.length()-2,expression.length()).equals("--")))
                expression.setText(expression.getText() + b.getText().toString());
    }

    public void negative(View view){

        TextView expression = (TextView) findViewById(R.id.text);
        if(expression.getText().toString().isEmpty()){
            expression.setText("-");
        }else {
            if(!expression.getText().toString().substring(0,1).equals("-"))
                expression.setText("-" + expression.getText().toString());
        }
    }

    public void delete(View view) {

        TextView expression = (TextView) findViewById(R.id.text);
        if(!expression.getText().toString().isEmpty())
            expression.setText(expression.getText().toString().substring(0, expression.getText().length() - 1));
    }

    public void clear(View view) {

        TextView expression = (TextView) findViewById(R.id.text);
        expression.setText("");

        TextView result = (TextView) findViewById(R.id.result);
        int o = getWindowManager().getDefaultDisplay().getRotation();
        if(o != 1 && o != 3) result.setTextSize(60);
        result.setText("0");
        //Toast.makeText(this,""+o,Toast.LENGTH_SHORT).show();
    }

    public double solveExpression(ArrayList<String> array){
        if(array.size()==1){
            return Double.valueOf(array.get(0)); //Devuelve el valor si no hay operaciones que realizar
        }else{
            int i = 1;
            boolean swm = false;
            while(i < array.size()-1 && !swm){ //Miramos si hay multiplicaciones y divisiones
                String s = array.get(i);
                if(s.equals("*") || s.equals("/")){
                    Double n1 = Double.valueOf(array.get(i-1));
                    Double n2 = Double.valueOf(array.get(i + 1));
                    if(s.equals("*")) array.set(i,""+(n1*n2));
                    if(s.equals("/")) array.set(i, "" + (n1 / n2));
                    array.remove(i-1);
                    array.remove(i);
                    swm = true;
                }
                i++;
            }

            if (!swm) {
                i = 1;
                boolean sw = false;
                while(i < array.size()-1 && !sw){ //Miramos si hay sumas y restas
                    String s = array.get(i);
                    if(s.equals("+") || s.equals("-")){
                        Double n1 = Double.valueOf(array.get(i-1));
                        Double n2 = Double.valueOf(array.get(i+1));
                        if(s.equals("+")) array.set(i, "" + (n1 + n2));
                        if(s.equals("-")) array.set(i,""+(n1-n2));
                        array.remove(i-1);
                        array.remove(i);
                        sw = true;
                    }
                    i++;
                }
            }
            return solveExpression(array); //Si aun no se han completado las operaciones vuelva hacer el proceso recursivamente
        }
    }

    public void solveExpression(View view){

        ArrayList<String> components;
        String expression;
        TextView  ex = (TextView) findViewById(R.id.text);
        expression = ex.getText().toString();
        TextView  result = (TextView) findViewById(R.id.result);
        if (!expression.isEmpty()) {
            int o = getWindowManager().getDefaultDisplay().getRotation();
            if(o != 1 && o != 3) result.setTextSize(60);
            try {
                components = new ArrayList<>();
                int i = 0;
                String unary = "";
                while (i < expression.length()) { //Recorremos la expresion sacando las operaciones y los operandos
                    int j = i;
                    boolean sw = false;
                    while (j < expression.length() && !sw) {
                        String s = expression.substring(j, j + 1);
                        if (s.equals("+") || s.equals("*") || s.equals("-") || s.equals("/")) {
                            if (s.equals("-") && expression.substring(i, j).isEmpty()) {
                                unary = s; //s = - si encuentra el simbolo unario
                            } else {
                                String se = unary + expression.substring(i, j);
                                if (!se.isEmpty()) components.add(se);
                                components.add(s);
                                unary = "";
                            }
                            sw = true;
                        }
                        j++; //continuo leyendo
                    }
                    if (j >= expression.length()) {
                        String s = unary + expression.substring(i, expression.length());
                        if (!s.isEmpty())
                            components.add(s); //Si termine de recorrer la expresion y no encontre una operacion, incluyo lo ultimo no reconocido como un operando
                    }
                    i = j;
                }
                try {
                    Double r = solveExpression(components); //Calculo el resultado, si hay un error se captura y se muestra por pantalla
                    result.setText(r.toString());
                } catch (Exception e) {
                    if(o != 1 && o != 3) result.setTextSize(40);
                    result.setText("Syntax error");
                }
            } catch (Exception e) {
                if(o != 1 && o != 3) result.setTextSize(40);
                result.setText("Syntax error");
            }
        }
    }

}
