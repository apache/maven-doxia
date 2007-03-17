package org.apache.maven.doxia.module.rtf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

class Monospace
    extends FontMetrics
{

    public static final CharMetrics[] metrics = {new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 236, -15, 364, 572 ), new CharMetrics( 600, 0, 187, 328, 413, 562 ),
        new CharMetrics( 600, 0, 93, -32, 507, 639 ), new CharMetrics( 600, 0, 105, -126, 496, 662 ),
        new CharMetrics( 600, 0, 81, -15, 518, 622 ), new CharMetrics( 600, 0, 63, -15, 538, 543 ),
        new CharMetrics( 600, 0, 213, 328, 376, 562 ), new CharMetrics( 600, 0, 269, -108, 440, 622 ),
        new CharMetrics( 600, 0, 160, -108, 331, 622 ), new CharMetrics( 600, 0, 116, 257, 484, 607 ),
        new CharMetrics( 600, 0, 80, 44, 520, 470 ), new CharMetrics( 600, 0, 181, -112, 344, 122 ),
        new CharMetrics( 600, 0, 103, 231, 497, 285 ), new CharMetrics( 600, 0, 229, -15, 371, 109 ),
        new CharMetrics( 600, 0, 125, -80, 475, 629 ), new CharMetrics( 600, 0, 106, -15, 494, 622 ),
        new CharMetrics( 600, 0, 96, 0, 505, 622 ), new CharMetrics( 600, 0, 70, 0, 471, 622 ),
        new CharMetrics( 600, 0, 75, -15, 466, 622 ), new CharMetrics( 600, 0, 78, 0, 500, 622 ),
        new CharMetrics( 600, 0, 92, -15, 497, 607 ), new CharMetrics( 600, 0, 111, -15, 497, 622 ),
        new CharMetrics( 600, 0, 82, 0, 483, 607 ), new CharMetrics( 600, 0, 102, -15, 498, 622 ),
        new CharMetrics( 600, 0, 96, -15, 489, 622 ), new CharMetrics( 600, 0, 229, -15, 371, 385 ),
        new CharMetrics( 600, 0, 181, -112, 371, 385 ), new CharMetrics( 600, 0, 41, 42, 519, 472 ),
        new CharMetrics( 600, 0, 80, 138, 520, 376 ), new CharMetrics( 600, 0, 66, 42, 544, 472 ),
        new CharMetrics( 600, 0, 129, -15, 492, 572 ), new CharMetrics( 600, 0, 77, -15, 533, 622 ),
        new CharMetrics( 600, 0, 3, 0, 597, 562 ), new CharMetrics( 600, 0, 43, 0, 559, 562 ),
        new CharMetrics( 600, 0, 41, -18, 540, 580 ), new CharMetrics( 600, 0, 43, 0, 574, 562 ),
        new CharMetrics( 600, 0, 53, 0, 550, 562 ), new CharMetrics( 600, 0, 53, 0, 545, 562 ),
        new CharMetrics( 600, 0, 31, -18, 575, 580 ), new CharMetrics( 600, 0, 32, 0, 568, 562 ),
        new CharMetrics( 600, 0, 96, 0, 504, 562 ), new CharMetrics( 600, 0, 34, -18, 566, 562 ),
        new CharMetrics( 600, 0, 38, 0, 582, 562 ), new CharMetrics( 600, 0, 47, 0, 554, 562 ),
        new CharMetrics( 600, 0, 4, 0, 596, 562 ), new CharMetrics( 600, 0, 7, -13, 593, 562 ),
        new CharMetrics( 600, 0, 43, -18, 557, 580 ), new CharMetrics( 600, 0, 79, 0, 558, 562 ),
        new CharMetrics( 600, 0, 43, -138, 557, 580 ), new CharMetrics( 600, 0, 38, 0, 588, 562 ),
        new CharMetrics( 600, 0, 72, -20, 529, 580 ), new CharMetrics( 600, 0, 38, 0, 563, 562 ),
        new CharMetrics( 600, 0, 17, -18, 583, 562 ), new CharMetrics( 600, 0, -4, -13, 604, 562 ),
        new CharMetrics( 600, 0, -3, -13, 603, 562 ), new CharMetrics( 600, 0, 23, 0, 577, 562 ),
        new CharMetrics( 600, 0, 24, 0, 576, 562 ), new CharMetrics( 600, 0, 86, 0, 514, 562 ),
        new CharMetrics( 600, 0, 269, -108, 442, 622 ), new CharMetrics( 600, 0, 118, -80, 482, 629 ),
        new CharMetrics( 600, 0, 158, -108, 331, 622 ), new CharMetrics( 600, 0, 94, 354, 506, 622 ),
        new CharMetrics( 600, 0, 0, -125, 600, -75 ), new CharMetrics( 600, 0, 224, 328, 387, 562 ),
        new CharMetrics( 600, 0, 53, -15, 559, 441 ), new CharMetrics( 600, 0, 14, -15, 575, 629 ),
        new CharMetrics( 600, 0, 66, -15, 529, 441 ), new CharMetrics( 600, 0, 45, -15, 591, 629 ),
        new CharMetrics( 600, 0, 66, -15, 548, 441 ), new CharMetrics( 600, 0, 114, 0, 531, 629 ),
        new CharMetrics( 600, 0, 45, -157, 566, 441 ), new CharMetrics( 600, 0, 18, 0, 582, 629 ),
        new CharMetrics( 600, 0, 95, 0, 505, 657 ), new CharMetrics( 600, 0, 82, -157, 410, 657 ),
        new CharMetrics( 600, 0, 43, 0, 580, 629 ), new CharMetrics( 600, 0, 95, 0, 505, 629 ),
        new CharMetrics( 600, 0, -5, 0, 605, 441 ), new CharMetrics( 600, 0, 26, 0, 575, 441 ),
        new CharMetrics( 600, 0, 62, -15, 538, 441 ), new CharMetrics( 600, 0, 9, -157, 555, 441 ),
        new CharMetrics( 600, 0, 45, -157, 591, 441 ), new CharMetrics( 600, 0, 60, 0, 559, 441 ),
        new CharMetrics( 600, 0, 80, -15, 513, 441 ), new CharMetrics( 600, 0, 87, -15, 530, 561 ),
        new CharMetrics( 600, 0, 21, -15, 562, 426 ), new CharMetrics( 600, 0, 10, -10, 590, 426 ),
        new CharMetrics( 600, 0, -4, -10, 604, 426 ), new CharMetrics( 600, 0, 20, 0, 580, 426 ),
        new CharMetrics( 600, 0, 7, -157, 592, 426 ), new CharMetrics( 600, 0, 99, 0, 502, 426 ),
        new CharMetrics( 600, 0, 182, -108, 437, 622 ), new CharMetrics( 600, 0, 275, -250, 326, 750 ),
        new CharMetrics( 600, 0, 163, -108, 418, 622 ), new CharMetrics( 600, 0, 63, 197, 540, 320 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 95, 0, 505, 426 ),
        new CharMetrics( 600, 0, 151, 497, 378, 672 ), new CharMetrics( 600, 0, 242, 497, 469, 672 ),
        new CharMetrics( 600, 0, 124, 477, 476, 654 ), new CharMetrics( 600, 0, 105, 489, 503, 606 ),
        new CharMetrics( 600, 0, 120, 525, 480, 565 ), new CharMetrics( 600, 0, 153, 501, 447, 609 ),
        new CharMetrics( 600, 0, 249, 477, 352, 580 ), new CharMetrics( 600, 0, 148, 492, 453, 595 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 218, 463, 382, 627 ),
        new CharMetrics( 600, 0, 224, -151, 362, 10 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 133, 497, 540, 672 ), new CharMetrics( 600, 0, 227, -151, 370, 0 ),
        new CharMetrics( 600, 0, 124, 492, 476, 669 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 236, -157, 364, 430 ), new CharMetrics( 600, 0, 96, -49, 500, 614 ),
        new CharMetrics( 600, 0, 84, -21, 521, 611 ), new CharMetrics( 600, 0, 73, 58, 527, 506 ),
        new CharMetrics( 600, 0, 26, 0, 574, 562 ), new CharMetrics( 600, 0, 275, -175, 326, 675 ),
        new CharMetrics( 600, 0, 113, -78, 488, 580 ), new CharMetrics( 600, 0, 148, 492, 453, 595 ),
        new CharMetrics( 600, 0, 0, -18, 600, 580 ), new CharMetrics( 600, 0, 156, 249, 442, 580 ),
        new CharMetrics( 600, 0, 37, 70, 563, 446 ), new CharMetrics( 600, 0, 87, 108, 513, 369 ),
        new CharMetrics( 600, 0, 103, 231, 497, 285 ), new CharMetrics( 600, 0, 0, -18, 600, 580 ),
        new CharMetrics( 600, 0, 120, 525, 480, 565 ), new CharMetrics( 600, 0, 123, 269, 477, 622 ),
        new CharMetrics( 600, 0, 87, 44, 513, 558 ), new CharMetrics( 600, 0, 177, 249, 424, 622 ),
        new CharMetrics( 600, 0, 155, 240, 406, 622 ), new CharMetrics( 600, 0, 242, 497, 469, 672 ),
        new CharMetrics( 600, 0, 21, -157, 562, 426 ), new CharMetrics( 600, 0, 50, -78, 511, 562 ),
        new CharMetrics( 600, 0, 222, 189, 378, 327 ), new CharMetrics( 600, 0, 224, -151, 362, 10 ),
        new CharMetrics( 600, 0, 172, 249, 428, 622 ), new CharMetrics( 600, 0, 157, 249, 443, 580 ),
        new CharMetrics( 600, 0, 37, 70, 563, 446 ), new CharMetrics( 600, 0, 0, -57, 600, 665 ),
        new CharMetrics( 600, 0, 0, -57, 611, 665 ), new CharMetrics( 600, 0, 8, -56, 593, 666 ),
        new CharMetrics( 600, 0, 108, -157, 471, 430 ), new CharMetrics( 600, 0, 3, 0, 597, 793 ),
        new CharMetrics( 600, 0, 3, 0, 597, 793 ), new CharMetrics( 600, 0, 3, 0, 597, 775 ),
        new CharMetrics( 600, 0, 3, 0, 597, 732 ), new CharMetrics( 600, 0, 3, 0, 597, 731 ),
        new CharMetrics( 600, 0, 3, 0, 597, 753 ), new CharMetrics( 600, 0, 3, 0, 550, 562 ),
        new CharMetrics( 600, 0, 41, -151, 540, 580 ), new CharMetrics( 600, 0, 53, 0, 550, 793 ),
        new CharMetrics( 600, 0, 53, 0, 550, 793 ), new CharMetrics( 600, 0, 53, 0, 550, 775 ),
        new CharMetrics( 600, 0, 53, 0, 550, 731 ), new CharMetrics( 600, 0, 96, 0, 504, 793 ),
        new CharMetrics( 600, 0, 96, 0, 504, 793 ), new CharMetrics( 600, 0, 96, 0, 504, 775 ),
        new CharMetrics( 600, 0, 96, 0, 504, 731 ), new CharMetrics( 600, 0, 30, 0, 574, 562 ),
        new CharMetrics( 600, 0, 7, -13, 593, 732 ), new CharMetrics( 600, 0, 43, -18, 557, 793 ),
        new CharMetrics( 600, 0, 43, -18, 557, 793 ), new CharMetrics( 600, 0, 43, -18, 557, 775 ),
        new CharMetrics( 600, 0, 43, -18, 557, 732 ), new CharMetrics( 600, 0, 43, -18, 557, 731 ),
        new CharMetrics( 600, 0, 87, 43, 515, 470 ), new CharMetrics( 600, 0, 43, -80, 557, 629 ),
        new CharMetrics( 600, 0, 17, -18, 583, 793 ), new CharMetrics( 600, 0, 17, -18, 583, 793 ),
        new CharMetrics( 600, 0, 17, -18, 583, 775 ), new CharMetrics( 600, 0, 17, -18, 583, 731 ),
        new CharMetrics( 600, 0, 24, 0, 576, 793 ), new CharMetrics( 600, 0, 79, 0, 538, 562 ),
        new CharMetrics( 600, 0, 48, -15, 588, 629 ), new CharMetrics( 600, 0, 53, -15, 559, 672 ),
        new CharMetrics( 600, 0, 53, -15, 559, 672 ), new CharMetrics( 600, 0, 53, -15, 559, 654 ),
        new CharMetrics( 600, 0, 53, -15, 559, 606 ), new CharMetrics( 600, 0, 53, -15, 559, 595 ),
        new CharMetrics( 600, 0, 53, -15, 559, 627 ), new CharMetrics( 600, 0, 19, -15, 570, 441 ),
        new CharMetrics( 600, 0, 66, -151, 529, 441 ), new CharMetrics( 600, 0, 66, -15, 548, 672 ),
        new CharMetrics( 600, 0, 66, -15, 548, 672 ), new CharMetrics( 600, 0, 66, -15, 548, 654 ),
        new CharMetrics( 600, 0, 66, -15, 548, 595 ), new CharMetrics( 600, 0, 95, 0, 505, 672 ),
        new CharMetrics( 600, 0, 95, 0, 505, 672 ), new CharMetrics( 600, 0, 94, 0, 505, 654 ),
        new CharMetrics( 600, 0, 95, 0, 505, 595 ), new CharMetrics( 600, 0, 62, -15, 538, 629 ),
        new CharMetrics( 600, 0, 26, 0, 575, 606 ), new CharMetrics( 600, 0, 62, -15, 538, 672 ),
        new CharMetrics( 600, 0, 62, -15, 538, 672 ), new CharMetrics( 600, 0, 62, -15, 538, 654 ),
        new CharMetrics( 600, 0, 62, -15, 538, 606 ), new CharMetrics( 600, 0, 62, -15, 538, 595 ),
        new CharMetrics( 600, 0, 87, 48, 513, 467 ), new CharMetrics( 600, 0, 62, -80, 538, 506 ),
        new CharMetrics( 600, 0, 21, -15, 562, 672 ), new CharMetrics( 600, 0, 21, -15, 562, 672 ),
        new CharMetrics( 600, 0, 21, -15, 562, 654 ), new CharMetrics( 600, 0, 21, -15, 562, 595 ),
        new CharMetrics( 600, 0, 7, -157, 592, 672 ), new CharMetrics( 600, 0, -6, -157, 555, 629 ),
        new CharMetrics( 600, 0, 7, -157, 592, 595 )};

    public Monospace()
    {
        super( true, 629, -157, new CharMetrics( 0, 0, -28, -250, 628, 805 ), metrics );
    }

}
