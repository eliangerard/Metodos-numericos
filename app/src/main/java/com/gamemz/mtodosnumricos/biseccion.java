package com.gamemz.mtodosnumricos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class biseccion extends AppCompatActivity {
    EditText function, xo, xu, eError;
    Button calculate;
    TextView valoresX, valoresE, txvIteraciones,resultado, txvTitle;
    CardView resultados;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    ScrollView scroll;

    List<Double> valoresI = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_biseccion);
        function = findViewById(R.id.etxtBFuncion);
        xo = findViewById(R.id.etxtBXo);
        xu = findViewById(R.id.etxtBXu);
        eError = findViewById(R.id.etxtBError);
        calculate = findViewById(R.id.btnCalcularB);
        valoresX = findViewById(R.id.txvBValoresX);
        valoresE = findViewById(R.id.txvBValoresE);
        txvIteraciones = findViewById(R.id.txvNumB);
        resultado = findViewById(R.id.txvResultadoB);
        resultados = findViewById(R.id.cvIteracionesB);
        txvTitle = findViewById(R.id.txvTitleB);
        txvTitle.setText("Bisección");
        graphView = findViewById(R.id.bGraph);
        scroll = findViewById(R.id.scrollB);
        graphView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    scroll.requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    scroll.requestDisallowInterceptTouchEvent(false);
                    return true;
                }
                return false;
            }
        });
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    double xm = 0, a = 0, b = 0;
                    double ei, es, x = 0, error;
                    int iteraciones = 0;
                    String tablaIteraciones = "", valoresDeX = "";
                    System.out.print("Ingresa la función: ");
                    String f = function.getText().toString();
                    Expression funcion = fromStringToFunction(f); // Conversion del string a funcion

                    // Solicitud de valores al usuario
                    ei = Double.parseDouble(xo.getText().toString());
                    es = Double.parseDouble(xu.getText().toString());
                    error = Double.parseDouble(eError.getText().toString());

                    // Se comprueba si f(a)*f(b) < 0, mientras no se ejecuta el metodo en cuestión
                    if(funcion.setVariable("x", ei).evaluate()*funcion.setVariable("x", es).evaluate()<0){
                        do{
                            a = funcion.setVariable("x", ei).evaluate(); // Se evaluan las funciones
                            b = funcion.setVariable("x", es).evaluate();

                            xm = (ei+es)/2; // Se calcula el punto medio
                            x = funcion.setVariable("x", xm).evaluate(); // Se evalua en base al punto medio

                            // Se verifica el signo del valor obtenido para conocer su posicion
                            if((a>0 && x<0) || (a<0 && x>0)){
                                es=xm;
                                ei=ei;
                            } else if ((b>0 && x<0) || (b<0 && x>0)){
                                ei = xm;
                                es = es;
                            }
                            iteraciones++; // Contador de iteraciones
                            tablaIteraciones +="#"+iteraciones+": "+xm+"\n";
                            valoresDeX+= "#"+iteraciones+": "+funcion.setVariable("x",xm).evaluate()+"\n";
                            valoresI.add(x);
                        } while(Math.abs(x)>error); // Mientras que el valor sea mayor que el error se ejecutará
                    }
                    graph(funcion, iteraciones);
                    txvIteraciones.setText(""+iteraciones);
                    valoresE.setText(tablaIteraciones);
                    valoresX.setText(valoresDeX);
                    resultado.setText(""+xm);
                    resultados.setVisibility(View.VISIBLE);
                }
                catch(Exception e){
                    Toast t = Toast.makeText(getApplicationContext(), "Revisa los datos introducidos", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
    }
    public static Expression fromStringToFunction(String expression) {
        Expression e = new ExpressionBuilder(expression).variables("x").build();
        return e;
    }
    public void graph(Expression fx, int iteraciones){
        graphView.removeAllSeries();
        double x = -25,y = 0;
        series = new LineGraphSeries<>();
        PointsGraphSeries<DataPoint> puntos = new PointsGraphSeries<>();
        Collections.sort(valoresI);
        for(int i = 0; i<iteraciones; i++){
            puntos.appendData(new DataPoint(valoresI.get(i),fx.setVariable("x", valoresI.get(i)).evaluate()), true, iteraciones);
        }
        graphView.addSeries(puntos);
        for(int i = 0; i<500; i++){
            x +=0.1;
            y = fx.setVariable("x",x).evaluate();
            series.appendData(new DataPoint(x,y),true,500);
        }
        graphView.addSeries(series);
        graphView.getViewport().setScalableY(true);
    }
}